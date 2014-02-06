import sbt._
import Keys._

object Resolvers {
  val twitter = Seq(
    "spray repo" at "http://repo.spray.io",
    "Matt's Github snapshots" at "http://mmacaulay.github.io/snapshots",
    "Matt's Github releases" at "http://mmacaulay.github.io/releases"
  )
}
 
object Dependencies {
  val twitter = Seq(
    "io.mca"            % "oauth" % "0.0.1-SNAPSHOT",
    "com.typesafe.akka" %% "akka-actor" % "2.2.3",
    "io.spray"          % "spray-client" % "1.2-RC4",
    "io.spray"          % "spray-httpx" % "1.2-RC4",
    "io.spray"          %% "spray-json" % "1.2.5",
    "commons-codec"     % "commons-codec" % "1.8",
    "org.scalatest"     %% "scalatest" % "2.0" % "test"
  )
}
 
object TwitterBuild extends Build {
  val Organization = "io.mca"
  val Version      = "0.0.1"
  val ScalaVersion = "2.10.0"
 
  lazy val Twitter = Project(
    id = "twitter",
    base = file("."),
    settings = defaultSettings ++ Seq(
      resolvers ++= Resolvers.twitter,
      libraryDependencies ++= Dependencies.twitter
    )
  )
 
  lazy val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := Organization,
    version      := Version,
    scalaVersion := ScalaVersion,
    crossPaths   := false
  )
  
  lazy val defaultSettings = buildSettings ++ Seq(
    scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked", "-feature"),
    javacOptions  ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")
  )
}


