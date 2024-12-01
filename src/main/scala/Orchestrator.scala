package aiven.kafka

import cats.effect.kernel.Concurrent
import cats.effect.{Async, Resource}
import fs2.Stream

sealed trait Orchestrator[F[_]] {

  def run: Stream[F, Unit]
}

case object Orchestrator {

  def create[F[_] : Concurrent](implicit F: Async[F]): Resource[F, Orchestrator[F]] = ???
}

