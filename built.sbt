name := "PixelServer"

version := "0.1"

organization := "freemonetize"

scalaVersion := "2.12.2"

sbtVersion := "0.13"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"           % "10.1.8",
  "com.typesafe.akka" %% "akka-stream"         % "2.5.19",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.19",
  "com.typesafe.akka" %% "akka-http-testkit"   % "10.1.8",
  "org.slf4j"         %  "slf4j-api"           % "1.7.26",
  "org.slf4j"         %  "slf4j-simple"        % "1.7.26",
  "com.lihaoyi"       %% "ujson"               % "0.7.4",
  "org.scalatest"     %% "scalatest"           % "3.0.7" % Test
)
