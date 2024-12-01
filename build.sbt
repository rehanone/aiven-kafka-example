import Dependencies.*

ThisBuild / scalaVersion := "3.3.4"


val compilerOptionsCommon = Seq(
  "-encoding",
  "UTF-8",
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings",
  "-language:higherKinds",
  "-language:postfixOps",
  "-Wconf:msg=lambda-parens:s"
)

val compilerOptionsScala3 = Seq(
  //  "-Xsource-features:leading-infix",
)

lazy val root = (project in file("."))
  .settings(
    name := "aiven-kafka-example",
    idePackagePrefix := Some("aiven.kafka")
  )
  .settings(
    scalacOptions ++= compilerOptionsCommon ++ PartialFunction
      .condOpt(CrossVersion.partialVersion(scalaVersion.value)) {
        case Some((3, _))            => compilerOptionsScala3
      }
      .toList
      .flatten,
    libraryDependencies ++= testDeps
      :+ fs2Core
      :+ fs2Io
      :+ fs2Kafka
  )
