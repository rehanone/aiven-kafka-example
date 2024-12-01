package aiven.kafka

import aiven.kafka.settings.AppSettings
import cats.effect.{ Async, Resource, Sync }
import cats.effect.kernel.Concurrent
import fs2.Stream
import fs2.kafka.*

sealed trait Orchestrator[F[_]] {

  def run: Stream[F, Unit]
}

case object Orchestrator {

  private def createConsumerSettings[F[_]](
    settings: AppSettings.ConsumerSettings
  )(implicit F: Sync[F]): Resource[F, ConsumerSettings[F, String, String]] =
    Resource.pure {
      ConsumerSettings(
        keyDeserializer = Deserializer[F, String],
        valueDeserializer = Deserializer[F, String]
      ).withAutoOffsetReset(AutoOffsetReset.Earliest)
        .withBootstrapServers(settings.bootstrapServers)
        .withGroupId(settings.group)
        .withProperties(
          "security.protocol"       -> settings.authentication.securityProtocol,
          "ssl.truststore.location" -> settings.authentication.truststoreLocation, // ".secrets/client.truststore.jks",
          "ssl.truststore.password" -> settings.authentication.truststorePassword, // "aiven-test",
          "ssl.keystore.type"       -> settings.authentication.keystoreType,
          "ssl.keystore.location"   -> settings.authentication.keystoreLocation,   // ".secrets/client.keystore.p12",
          "ssl.keystore.password"   -> settings.authentication.keystorePassword,
          "ssl.key.password"        -> settings.authentication.keyPassword
        )
    }

  def create[F[_]: Concurrent](implicit F: Async[F]): Resource[F, Orchestrator[F]] =
    for {
      appSettings      <- AppSettings.load
      consumerConfig   <- Resource.pure(appSettings.aiven.stream.consumer)
      consumerSettings <- createConsumerSettings(consumerConfig)
      kafkaTopic       <- Resource.pure(consumerConfig.topic)
    } yield new Orchestrator[F] {
      override def run: Stream[F, Unit] = {

        def processRecord(record: ConsumerRecord[String, String]): F[Unit] =
          F.delay(println(s"Processing record: $record"))

        KafkaConsumer
          .stream(consumerSettings)
          .subscribeTo(kafkaTopic)
          .partitionedRecords
          .map { partitionStream =>
            partitionStream.evalMap { committable =>
              processRecord(committable.record)
            }
          }
          .parJoinUnbounded
      }
    }
}
