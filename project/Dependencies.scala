import sbt.*

object Dependencies {
  // Versions
  lazy val fs2Version                   = "3.11.0"
  lazy val fs2KafkaVersion              = "3.6.0"
  lazy val scalatestVersion             = "3.2.19"

  // Libraries
  val fs2Core = "co.fs2" %% "fs2-core" % fs2Version
  val fs2Io   = "co.fs2" %% "fs2-io"   % fs2Version

  val fs2Kafka = "com.github.fd4s" %% "fs2-kafka" % fs2KafkaVersion

  // Test Libraries
  val scalatest         = "org.scalatest" %% "scalatest"           % scalatestVersion

  // Groups
  val testDeps =
    Seq(scalatest % Test)
}
