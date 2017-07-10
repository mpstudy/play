name := """play-java-rest-api-example"""

version := "2.6.x"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion in ThisBuild := "2.11.11"

// Guice (pronounced 'juice') is a lightweight dependency injection framework for Java 6 and above, brought to you by Google.
libraryDependencies += guice

PlayKeys.externalizeResources := false
