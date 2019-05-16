ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0"
ThisBuild / organization     := "io.github.viniarck"

// async TCP actors
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.22"

// logging
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

// serialization
libraryDependencies += "com.lihaoyi" %% "upickle" % "0.7.1"
libraryDependencies += "com.lihaoyi" %% "upack" % "0.7.1"
libraryDependencies += "org.msgpack" %% "msgpack-scala" % "0.8.13"
