name := """data-store"""

version := "1.0-SNAPSHOT"

lazy val common = RootProject(file("../common"))

lazy val auth = RootProject(file("../auth-plugin"))

lazy val root = (project in file(".")).enablePlugins(PlayJava)
    .aggregate(auth, common)
    .dependsOn(auth % "test->test;compile->compile", common % "test->test;compile->compile")

TwirlKeys.templateImports += "com.fasterxml.jackson.databind.node.ObjectNode"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
