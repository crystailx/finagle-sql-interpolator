import sbt._

object Dependencies {

  private val log4jVersion = "2.19.0"
  private val scalaLoggingVersion = "3.9.5"
  private val finagleVersion = "22.7.0"
  private val scalatestVersion = "3.2.14"
  private val scalacticVersion = scalatestVersion
  private val scalamockVersion = "5.2.0"

  def log: Seq[ModuleID] = Seq(
    "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4jVersion,
    "org.apache.logging.log4j" % "log4j-api" % log4jVersion,
    "org.apache.logging.log4j" % "log4j-core" % log4jVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
  )

  def core: Seq[ModuleID] = Seq("com.twitter" %% "util-core" % finagleVersion)

  def mysql: Seq[ModuleID] = Seq("com.twitter" %% "finagle-mysql" % finagleVersion)

  def postgresql: Seq[ModuleID] = Seq("com.twitter" %% "finagle-postgresql" % finagleVersion)

  def test: Seq[ModuleID] =
    Seq(
      "org.scalatest" %% "scalatest" % scalatestVersion,
      "org.scalactic" %% "scalactic" % scalacticVersion,
      "org.scalamock" %% "scalamock" % scalamockVersion,
      "org.mock-server" % "mockserver-netty" % "5.14.0"
    )
}
