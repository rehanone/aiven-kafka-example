package aiven.kafka

import cats.effect.{Async, Resource, Sync}
import cats.effect.kernel.Concurrent
import fs2.Stream
import fs2.kafka.*

sealed trait Orchestrator[F[_]] {

  def run: Stream[F, Unit]
}

case object Orchestrator {

  private def createConsumerSettings[F[_]]()(implicit F: Sync[F]): Resource[F, ConsumerSettings[F, String, String]] = Resource.pure {
    ConsumerSettings(
      keyDeserializer = Deserializer[F, String],
      valueDeserializer = Deserializer[F, String]
    ).withAutoOffsetReset(AutoOffsetReset.Earliest)
      .withBootstrapServers("kafka-2debecd3-ink-8f46.f.aivencloud.com:14844")
      .withGroupId("aiven")
      .withProperties(
        "security.protocol" -> "SSL",
        "ssl.truststore.location" -> "client.truststore.jks",
        "ssl.truststore.password" -> "aiven-test",
        "ssl.keystore.type" -> "PKCS12",
        "ssl.keystore.location" -> "client.keystore.p12",
        "ssl.keystore.password" -> "",
        "ssl.key.password" -> "",
      )
  }

  def create[F[_] : Concurrent](implicit F: Async[F]): Resource[F, Orchestrator[F]] =
    for {
      //      appSettings            <- AppSettings.load
      consumerSettings <- createConsumerSettings()
      kafkaTopic <- Resource.pure("default_topic")
    } yield new Orchestrator[F] {
      override def run: Stream[F, Unit] = {

        def processRecord(record: ConsumerRecord[String, String]): F[Unit] = {
          F.delay(println(s"Processing record: $record"))
        }

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

