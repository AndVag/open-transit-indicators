name := "Open Transit Indicators - GeoTrellis components"

version := "0.1"

scalaVersion := "2.10.3"

// Useful tool for scala web development. use "./sbt ~re-start" and
// it will recompile and run the server after each save
seq(Revolver.settings: _*)

libraryDependencies ++= Seq(
  "io.spray" % "spray-routing" % "1.2.0",
  "io.spray" % "spray-can" % "1.2.0",
  "com.azavea.geotrellis" %% "geotrellis" % "0.9.1",
  // The following package is still in development, but will be published to Maven shortly.
  // In the meantime, the code must be compiled and published locally in order to work.
  // Use the following steps to do so:
  //   1) git clone https://github.com/echeipesh/gtfs-parser.git
  //   2) cd gtfs-parser
  //   3) git checkout -b feature/slick origin/feature/slick
  //   4) ./sbt
  //   5) publish-local
  // These steps haven't been added to the provisioning script, because they will soon be obsolete.
  // Note: make sure to perform these commands on the same machine/VM where GeoTrellis is running.
  "com.azavea" %% "gtfs-parser" % "0.1-SNAPSHOT",
  "com.github.nscala-time" %% "nscala-time" % "0.8.0",
  "com.typesafe.slick" %% "slick" % "2.0.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "postgresql" % "postgresql" % "9.1-901.jdbc4"
)
