ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

// Don't forget to add the assembly plugin in ./project/plugins.sbt since you need
// to build a fat JAR
mainClass in assembly := Some("testnvimhost.MyApp")

// This is the fat JAR that nvim will spawn when the first function call is made
assemblyJarName in assembly := "demoplugin.jar"
    
libraryDependencies += "io.github.viniarck" %% "nvimhost-scala" % "1.0.0"
