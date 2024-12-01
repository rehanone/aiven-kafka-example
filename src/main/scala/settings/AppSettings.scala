package aiven.kafka
package settings

import settings.AppSettings.AivenSettings

import cats.effect.{ Resource, Sync }

import cats.effect.unsafe.implicits.global
import io.circe.generic.auto._
import io.circe.config.parser

case class AppSettings(aiven: AivenSettings) {}

object AppSettings {

  case class AuthenticationSettings(
    securityProtocol: String,
    truststoreLocation: String,
    truststorePassword: String,
    keystoreType: String,
    keystoreLocation: String,
    keystorePassword: String,
    keyPassword: String
  )

  case class ConsumerSettings(
    bootstrapServers: String,
    group: String,
    topic: String,
    authentication: AuthenticationSettings
  )

  case class StreamSettings(
    consumer: ConsumerSettings
  )

  case class AivenSettings(
    stream: StreamSettings
  )

  def load[F[_]](implicit F: Sync[F]): Resource[F, AppSettings] =
    Resource.eval {
      parser.decodeF[F, AppSettings]()
    }
}
