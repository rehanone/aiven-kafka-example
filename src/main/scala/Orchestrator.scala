package aiven.kafka

import aiven.kafka.common.time.TimeConverter
import cats.implicits.*
import aiven.kafka.settings.AppSettings
import cats.effect.{ Async, Resource, Sync }
import cats.effect.kernel.{ Clock, Concurrent }
import fs2.Stream
import fs2.kafka.*

import java.util.UUID

sealed trait Orchestrator[F[_]] {

  def run: Stream[F, Unit]
}

case object Orchestrator {

  private def createConsumerSettings[F[_]](
    settings: AppSettings.ConsumerSettings
  )(implicit F: Sync[F]): Resource[F, ConsumerSettings[F, String, String]] =
    Resource.pure {
      ConsumerSettings[F, String, String]
        .withAutoOffsetReset(AutoOffsetReset.Earliest)
        .withBootstrapServers(settings.bootstrapServers)
        .withGroupId(settings.group)
        .withProperties(
          "security.protocol"       -> settings.authentication.securityProtocol,
          "ssl.truststore.location" -> settings.authentication.truststoreLocation,
          "ssl.truststore.password" -> settings.authentication.truststorePassword,
          "ssl.keystore.type"       -> settings.authentication.keystoreType,
          "ssl.keystore.location"   -> settings.authentication.keystoreLocation,
          "ssl.keystore.password"   -> settings.authentication.keystorePassword,
          "ssl.key.password"        -> settings.authentication.keyPassword
        )
    }

  private def createProducerSettings[F[_]](
    settings: AppSettings.ProducerSettings
  )(implicit F: Sync[F]): Resource[F, ProducerSettings[F, String, String]] =
    Resource.pure {
      ProducerSettings[F, String, String]
        .withBootstrapServers(settings.bootstrapServers)
        .withProperties(
          "security.protocol"       -> settings.authentication.securityProtocol,
          "ssl.truststore.location" -> settings.authentication.truststoreLocation,
          "ssl.truststore.password" -> settings.authentication.truststorePassword,
          "ssl.keystore.type"       -> settings.authentication.keystoreType,
          "ssl.keystore.location"   -> settings.authentication.keystoreLocation,
          "ssl.keystore.password"   -> settings.authentication.keystorePassword,
          "ssl.key.password"        -> settings.authentication.keyPassword
        )
    }

  def create[F[_]: Concurrent](implicit F: Async[F]): Resource[F, Orchestrator[F]] =
    for {
      appSettings         <- AppSettings.load
      consumerConfig      <- Resource.pure(appSettings.aiven.stream.consumer)
      consumerSettings    <- createConsumerSettings(consumerConfig)
      producerConfig      <- Resource.pure(appSettings.aiven.stream.producer)
      producerSettings    <- createProducerSettings(producerConfig)
      pizzaOrdersTopic    <- Resource.pure(consumerConfig.topic)
      pizzaOrdersAckTopic <- Resource.pure(producerConfig.topic)
    } yield new Orchestrator[F] {
      override def run: Stream[F, Unit] = {

        def processRecord(
          committable: CommittableConsumerRecord[F, String, String]
        ): F[ProducerRecord[String, String]] = {
          import TimeConverter.*

          for {
            key        <- F.delay(committable.record.key)
            value      <- F.delay(committable.record.value)
            timestamp  <- Clock[F].realTime.map(_.toMillis.format(DATE_FORMAT_ISO8601))
            ackId      <- F.delay(UUID.randomUUID())
            ackKey     <- F.delay {
                            s"""
                           |{
                           |  "id": "$ackId",
                           |  "timestamp": "$timestamp"
                           |}
                           |""".stripMargin
                          }
            ackMessage <- F.delay {
                            s"""
                               |{
                               |  "status": "processed",
                               |  "source": {
                               |    "key": $key,
                               |    "topic": "${committable.offset.topicPartition.topic()}",
                               |    "partition": ${committable.offset.topicPartition.partition()},
                               |    "offset": ${committable.offset.offsetAndMetadata.offset()}
                               |  }
                               |}
                               |""".stripMargin
                          }
            ackRecord  <- F.delay(ProducerRecord(pizzaOrdersAckTopic, ackKey, value = ackMessage))
            _          <- F.delay(println(s"Processing record key: $key with payload $value"))
            _          <- F.delay(println(s"Publishing Ack: $ackKey with payload $ackMessage"))
          } yield ackRecord
        }

        KafkaConsumer
          .stream(consumerSettings)
          .subscribeTo(pizzaOrdersTopic)
          .records
          .evalMap { committable =>
            processRecord(committable)
          }
          .map { ack =>
            ProducerRecords.one(ack)
          }
          .through(KafkaProducer.pipe(producerSettings))
          .as(F.unit)
      }
    }
}
