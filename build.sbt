name := """dux"""

version := "1.0-SNAPSHOT"

lazy val common = RootProject(file("common"))
lazy val auth = RootProject(file("auth-plugin"))
lazy val leaderboard = RootProject(file("leaderboard"))

scalaVersion := "2.11.6"
