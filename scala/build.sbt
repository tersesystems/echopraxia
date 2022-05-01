import sbt.Keys._

val scala213      = "2.13.8"
val scala212      = "2.12.14"
val scalaVersions = Seq(scala212, scala213)

initialize := {
  val _        = initialize.value // run the previous initialization
  val required = "11"
  val current  = sys.props("java.specification.version")
  assert(current >= required, s"Unsupported JDK: java.specification.version $current != $required")
}

ThisBuild / organization := "com.tersesystems.echopraxia"
ThisBuild / homepage     := Some(url("https://tersesystems.github.io/blindsight"))

ThisBuild / startYear := Some(2021)
ThisBuild / licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/tersesystems/echopraxia"),
    "scm:git@github.com:tersesystems/echopraxia.git"
  )
)

ThisBuild / versionScheme := Some("early-semver")

ThisBuild / resolvers += Resolver.mavenLocal
ThisBuild / scalaVersion       := scala213
ThisBuild / crossScalaVersions := scalaVersions
ThisBuild / scalacOptions      := scalacOptionsVersion(scalaVersion.value)

ThisBuild / Test / parallelExecution := false
Global / concurrentRestrictions += Tags.limit(Tags.Test, 1)

lazy val api = (project in file("api"))
  .settings(
    name := "scala-api",
    //
    libraryDependencies += "com.tersesystems.echopraxia" % "api"                % version.value,
    libraryDependencies += "org.scala-lang.modules"     %% "scala-java8-compat" % "1.0.2",
    libraryDependencies += "org.scala-lang.modules" %% "scala-collection-compat" % "2.7.0",
    libraryDependencies += "com.daodecode"          %% "scalaj-collection"       % "0.3.1"
  )

lazy val logger = (project in file("sourcecode"))
  .settings(
    name := "scala-logger",
    //
    libraryDependencies += "com.lihaoyi" %% "sourcecode" % "0.2.8",
    //
    libraryDependencies += "com.tersesystems.echopraxia" % "logstash"  % version.value % Test,
    libraryDependencies += "org.scalatest"              %% "scalatest" % "3.2.11"      % Test
  )
  .dependsOn(api % "compile->compile;test->compile")

lazy val asyncLogger = (project in file("sourcecode-async"))
  .settings(
    name := "scala-async-logger",
    //
    libraryDependencies += "com.lihaoyi" %% "sourcecode" % "0.2.8",
    //
    libraryDependencies += "com.tersesystems.echopraxia" % "logstash"  % version.value % Test,
    libraryDependencies += "org.scalatest"              %% "scalatest" % "3.2.11"      % Test
  )
  .dependsOn(api % "compile->compile;test->compile")

lazy val root = (project in file("."))
  .settings(
    Compile / doc / sources                := Seq.empty,
    Compile / packageDoc / publishArtifact := false,
    publishArtifact                        := false,
    publish / skip                         := true
  )
  .aggregate(api, logger, asyncLogger)

def scalacOptionsVersion(scalaVersion: String): Seq[String] = {
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, n)) if n >= 13 =>
      Seq(
        "-unchecked",
        "-deprecation",
        "-feature",
        "-encoding",
        "UTF-8",
        "-language:implicitConversions",
        "-language:higherKinds",
        "-language:existentials",
        "-language:postfixOps",
        "-Xlint",
        "-Ywarn-dead-code",
        "-Yrangepos",
        "-Xsource:2.13",
        "-release",
        "8"
      ) ++ optimizeInline
    case Some((2, n)) if n == 12 =>
      Seq(
        "-unchecked",
        "-deprecation",
        "-feature",
        "-encoding",
        "UTF-8",
        "-language:implicitConversions",
        "-language:higherKinds",
        "-language:existentials",
        "-language:postfixOps",
        "-Xlint",
        "-Ywarn-dead-code",
        "-Yrangepos",
        "-Xsource:2.12",
        "-Yno-adapted-args",
        "-release",
        "8"
      ) ++ optimizeInline

  }
}

val optimizeInline = Seq(
  "-opt:l:inline",
  "-opt-inline-from:com.tersesystems.echopraxia.**",
  "-opt-warnings:any-inline-failed"
)
