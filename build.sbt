name := """dux"""

version := "1.0-SNAPSHOT"

lazy val leaderboard = RootProject(file("leaderboard"))
lazy val dataStore = RootProject(file("data-store"))
lazy val authentication = RootProject(file("authentication"))
lazy val common = RootProject(file("common"))
lazy val authPlugin = RootProject(file("auth-plugin"))

scalaVersion := "2.11.6"
