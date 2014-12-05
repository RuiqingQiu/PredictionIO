import AssemblyKeys._

assemblySettings

name := "example-scala-recommendations"

organization := "io.prediction"

libraryDependencies ++= Seq(
  "io.prediction"    %% "core"          % "0.8.1-SNAPSHOT" % "provided",
  "commons-io"        % "commons-io"    % "2.4",
  "org.apache.spark" %% "spark-core"    % "1.1.0" % "provided",
  "org.apache.spark" %% "spark-graphx"  % "1.1.0" % "provided",
  "org.json4s"       %% "json4s-native" % "3.2.10")
