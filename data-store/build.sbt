name := """data-store"""

version := "1.0-SNAPSHOT"

lazy val common = RootProject(file("../common"))

lazy val root = (project in file(".")).enablePlugins(PlayScala)
    .aggregate(common)
    .dependsOn(common % "test->test;compile->compile")

TwirlKeys.templateImports += "com.fasterxml.jackson.databind.node.ObjectNode"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"


// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
