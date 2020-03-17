scalaVersion     := "2.13.1"
version          := "1.1.0"
organization     := "io.github.viniarck"
name             := "nvimhost-scala"

// async TCP actors
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.4"

// logging
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

// serialization
libraryDependencies += "com.lihaoyi" %% "upickle" % "1.0.0"
libraryDependencies += "com.lihaoyi" %% "upack" % "1.0.0"
libraryDependencies += "org.msgpack" % "msgpack-scala_2.13.0-M2" % "0.8.13"

// publish
publishTo := sonatypePublishToBundle.value
