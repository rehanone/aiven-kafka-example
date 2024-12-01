package aiven.kafka

import cats.effect.*
import fs2.*
import scala.concurrent.duration.*

object App extends IOApp.Simple {

  def run: IO[Unit] =
    val v1 = Stream
      .emit[IO, Long](System.currentTimeMillis())
    val v2 = Stream
      .awakeDelay[IO](10 seconds)

    val v3 = v1 zip v2

    v2.flatMap { x =>
        Stream.eval(Sync[IO].delay(println(x)))
      }
      .compile
      .drain
}
