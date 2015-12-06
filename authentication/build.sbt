name := """authentication"""

version := "1.0-SNAPSHOT"

lazy val common = RootProject(file("../common"))

lazy val auth = RootProject(file("../auth-plugin"))

lazy val root = (project in file(".")).enablePlugins(PlayScala)
    .aggregate(auth, common)
    .dependsOn(auth % "test->test;compile->compile", common % "test->test;compile->compile")

lazy val setup = taskKey[Unit]("Setup dependencies")

setup := {
  "sh setup.sh" !
}

TwirlKeys.templateImports += "com.fasterxml.jackson.databind.node.ObjectNode"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test,
  "com.typesafe.play.modules" %% "play-modules-redis" % "2.4.1",
  "org.xerial" % "sqlite-jdbc" % "3.8.11.2"
)

resolvers += "google-sedis-fix" at "http://pk11-scratch.googlecode.com/svn/trunk"
resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
