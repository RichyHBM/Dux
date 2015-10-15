name := """leaderboards"""

version := "1.0-SNAPSHOT"

lazy val common = RootProject(file("../common"))

lazy val root = (project in file(".")).enablePlugins(PlayJava).aggregate(common).dependsOn(common)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "com.typesafe.play.modules" %% "play-modules-redis" % "2.4.1"
)

resolvers += "google-sedis-fix" at "http://pk11-scratch.googlecode.com/svn/trunk"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
