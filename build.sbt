import Dependencies._
import com.jsuereth.sbtpgp.PgpKeys.publishSigned

lazy val scala213 = "2.13.10"
lazy val scala212 = "2.12.17"
lazy val scala211 = "2.11.12"
lazy val supportedScalaVersions = Seq(scala213, scala212 /*, scala211*/ )
ThisBuild / organization := "io.github.crystailx"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := scala213
ThisBuild / crossScalaVersions := supportedScalaVersions
ThisBuild / trackInternalDependencies := TrackLevel.TrackAlways
//ThisBuild / exportJars := true
ThisBuild / libraryDependencies ++= Dependencies.test.map(_ % Test)

// sonatype publish
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / publishTo := sonatypePublishToBundle.value

scalacOptions := Seq(
  "-unchecked",
  "-deprecation",
  "-feature"
)

lazy val moduleSettings = Seq(
  crossScalaVersions := supportedScalaVersions,
  libraryDependencies ++= Dependencies.test
    .map(_ % Test) ++ Dependencies.log
)

lazy val noPublishSettings = Seq(
  publish / skip := true,
  publish := {},
  publishLocal := {},
  publishSigned := {}
)

lazy val core = (project in file("core"))
  .settings(name := "finagle-interpolator-core")
  .settings(moduleSettings)
  /*.settings(libraryDependencies ++= (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, scalaMajor)) if scalaMajor == 12 =>
      Seq("com.chuusai" %% "shapeless" % "2.3.10")
    case _ => Seq.empty
  }))*/

lazy val coreWithTest = core % "compile->compile;test->test"

lazy val mysql = (project in file("finagle-interpolator-mysql"))
  .settings(name := "finagle-interpolator-mysql")
  .settings(moduleSettings)
  .settings(libraryDependencies ++= Dependencies.mysql)
  .dependsOn(core)

lazy val postgres = (project in file("finagle-interpolator-postgresql"))
  .settings(name := "finagle-interpolator-postgresql")
  .settings(moduleSettings)
  .settings(libraryDependencies ++= Dependencies.postgresql)
  .dependsOn(core)

lazy val root = (project in file("."))
  .settings(noPublishSettings)
  .settings(
    crossScalaVersions := Nil,
    update / aggregate := false
  )
  .aggregate(
    mysql
  )
