import sbt.*

object Dependencies {
  // Versions
  lazy val catsEffectVersion      = "3.5.7"
  lazy val fs2Version             = "3.11.0"
  lazy val fs2KafkaVersion        = "3.6.0"
  lazy val circeVersion           = "0.14.10"
  lazy val circeConfigVersion     = "0.10.1"
  lazy val munitVersion           = "1.0.2"
  lazy val munitCatsEffectVersion = "2.0.0-M1"
  lazy val scalatestVersion       = "3.2.19"
  lazy val typesafeConfigVersion  = "1.4.3"

  // Libraries
  val fs2Core = "co.fs2" %% "fs2-core" % fs2Version
  val fs2Io   = "co.fs2" %% "fs2-io"   % fs2Version

  val fs2Kafka = "com.github.fd4s" %% "fs2-kafka" % fs2KafkaVersion

  val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
  val circeLiteral = "io.circe" %% "circe-literal" % circeVersion
  val circeParser  = "io.circe" %% "circe-parser"  % circeVersion
  val circeConfig  = "io.circe" %% "circe-config"  % circeConfigVersion

  // Java Libraries
  val typesafeConfig = "com.typesafe" % "config" % typesafeConfigVersion

  // Test Libraries
  val catsEffectTestkit = "org.typelevel" %% "cats-effect-testkit" % catsEffectVersion
  val munitCore         = "org.scalameta" %% "munit"               % munitVersion
  val munitCatsEffect   = "org.typelevel" %% "munit-cats-effect"   % munitCatsEffectVersion
  val scalatest         = "org.scalatest" %% "scalatest"           % scalatestVersion

  // Groups
  val munitDeps =
    Seq(munitCore % Test, catsEffectTestkit % Test, munitCatsEffect % Test)

  val testDeps =
    Seq(scalatest % Test)
}
