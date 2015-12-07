name := """dux"""

version := "1.0-SNAPSHOT"

lazy val common = RootProject(file("common"))
lazy val authPlugin = RootProject(file("auth-plugin"))
lazy val leaderboard = RootProject(file("leaderboard"))
lazy val dataStore = RootProject(file("data-store"))
lazy val authentication = RootProject(file("authentication"))

scalaVersion := "2.11.6"
