name := """auth-plugin"""

version := "1.0-SNAPSHOT"

lazy val authPlugin = (project in file(".")).enablePlugins(PlayScala)
    .aggregate(common)
    .dependsOn(common % "test->test;compile->compile")

lazy val common = RootProject(file("../common"))

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  cache,
  ws,
  specs2 % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
