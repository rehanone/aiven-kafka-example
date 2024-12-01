package aiven.kafka
package settings

import cats.effect.IO
import munit.*

import settings.AppSettings.*

class AppSettingsSpec extends CatsEffectSuite {

  test("load default config") {

    val loaded = AppSettings.load[IO].use {
      IO(_)
    }

    val expected = AppSettings(
      aiven = AivenSettings(
        stream = StreamSettings(
          consumer = ConsumerSettings(
            bootstrapServers = "kafka-2debecd3-ink-8f46.f.aivencloud.com:14844",
            group = "aiven",
            topic = "default_topic",
            authentication = AuthenticationSettings(
              securityProtocol = "SSL",
              truststoreLocation = "client.truststore.jks",
              truststorePassword = "",
              keystoreType = "PKCS12",
              keystoreLocation = "client.keystore.p12",
              keystorePassword = "",
              keyPassword = ""
            )
          )
        )
      )
    )

    assertIO(loaded, expected)
  }
}
