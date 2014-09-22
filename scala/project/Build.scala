import sbt._
import sbt.Keys._

object Build extends Build {
  lazy val gtfs = 
    Project("gtfs", file("gtfs"))
      .settings(
        name := "gtfs",
        organization := "com.azavea",
        version := "0.1-SNAPSHOT",
        scalaVersion := "2.10.3",
        scalacOptions ++=
          Seq("-deprecation",
            "-unchecked",
            "-Yinline-warnings",
            "-language:implicitConversions",
            "-language:reflectiveCalls",
            "-language:postfixOps",
            "-language:existentials",
            "-feature"),

      libraryDependencies ++=
        Seq(
          "org.apache.servicemix.bundles" % "org.apache.servicemix.bundles.commons-csv" % "1.0-r706900_3",
          
          "org.scalatest" % "scalatest_2.10" % "2.1.0" % "test",
          "com.github.nscala-time" %% "nscala-time" % "0.8.0",

          "commons-io" % "commons-io" % "2.4",

          "com.azavea.geotrellis" %% "geotrellis-vector" % "0.10.0-SNAPSHOT",
          "com.azavea.geotrellis" %% "geotrellis-proj4" % "0.10.0-SNAPSHOT",
          "com.azavea.geotrellis" %% "geotrellis-slick" % "0.10.0-SNAPSHOT",

          "org.joda" % "joda-convert" % "1.5",
          "com.github.tototoshi" %% "slick-joda-mapper" % "1.2.0"
        )
    )

  lazy val opentransit =
    Project("opentransit", file("opentransit"))
      .settings(
        name := "gtfs-parser",
        organization := "com.azavea",
        version := "0.1-SNAPSHOT",
        scalaVersion := "2.10.3",
        libraryDependencies ++= Seq(
          "io.spray" % "spray-routing" % "1.2.0",
          "io.spray" % "spray-can" % "1.2.0",
          "io.spray" % "spray-client" % "1.2.0",
          "io.spray" %% "spray-json" % "1.2.6",
          "io.spray" % "spray-httpx" % "1.2.0",
          "com.typesafe.akka" %% "akka-actor" % "2.2.4",
          "com.github.nscala-time" %% "nscala-time" % "1.4.0",
          "org.scalatest" %% "scalatest" % "2.1.5" % "test",
          "org.slf4j" % "slf4j-nop" % "1.6.4",
          "org.scala-lang" % "scala-compiler" % "2.10.3"
        )
       ) 
      .settings(spray.revolver.RevolverPlugin.Revolver.settings:_*)
      .dependsOn(gtfs)
}
