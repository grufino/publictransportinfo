import sbt.Keys._
import play.sbt.PlaySettings

lazy val root = (project in file("."))
  .enablePlugins(PlayService, PlayLayoutPlugin, Common)
  .settings(
    name := "publictransportinfo",
    scalaVersion := "2.12.8",
    libraryDependencies ++= Seq(
      guice,
      "org.joda" % "joda-convert" % "2.2.1",
      "io.lemonlabs" %% "scala-uri" % "1.5.1",
      "net.codingwell" %% "scala-guice" % "4.2.6",
      "org.apache.spark" %% "spark-streaming" % "2.4.3",
      "org.apache.spark" %% "spark-sql" % "2.4.3",
      "com.typesafe.akka" %% "akka-stream-kafka" % "2.0.3",
      "com.typesafe.akka" %% "akka-stream" % "2.5.30",
      "org.apache.kafka" %% "kafka" % "2.4.1",
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
    ).map(_.exclude("org.slf4j", "*")),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  ).settings(libraryDependencies ++= Seq("net.logstash.logback" % "logstash-logback-encoder" % "6.2"))

PlayKeys.devSettings := Seq("play.server.http.port" -> "8081")