package aiven.kafka

import cats.effect.{ ExitCode, IO, IOApp }
import fs2.*

object App extends IOApp.Simple {

  def run: IO[Unit] =
    Stream
      .resource {
        Orchestrator
          .create[IO]
      }
      .flatMap(_.run)
      .compile
      .drain
      .as(ExitCode.Success)
}
