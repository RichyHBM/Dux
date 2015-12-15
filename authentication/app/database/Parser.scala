package database

import java.sql.ResultSet
import database.models._

object Parser {
  def toApp(resultSet: ResultSet) {
    new App(
      resultSet.getInt(Structure.Apps.Columns.Id),
      resultSet.getString(Structure.Apps.Columns.AppIdentifier),
      resultSet.getString(Structure.Apps.Columns.FullName),
      resultSet.getString(Structure.Apps.Columns.Description)
    )
  }

  def toGroup(resultSet: ResultSet) {
    new Group(
      resultSet.getInt(Structure.Groups.Columns.Id),
      resultSet.getString(Structure.Groups.Columns.Name),
      resultSet.getString(Structure.Groups.Columns.Description)
    )
  }

  def toPermission(resultSet: ResultSet) {
    new Permission(
      resultSet.getInt(Structure.Permissions.Columns.Id),
      resultSet.getString(Structure.Permissions.Columns.Name),
      resultSet.getString(Structure.Permissions.Columns.Description)
    )
  }

  def toUser(resultSet: ResultSet) {
    new User(
      resultSet.getInt(Structure.Users.Columns.Id),
      resultSet.getString(Structure.Users.Columns.Name),
      resultSet.getString(Structure.Users.Columns.Email),
      resultSet.getBytes(Structure.Users.Columns.Password).toList,
      resultSet.getBytes(Structure.Users.Columns.Salt).toList,
      resultSet.getString(Structure.Users.Columns.ApiKey),
      resultSet.getDate(Structure.Users.Columns.CreatedOn),
      resultSet.getInt(Structure.Users.Columns.FailedAttempts),
      resultSet.getBoolean(Structure.Users.Columns.Blocked)
    )
  }
}