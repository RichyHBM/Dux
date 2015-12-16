package database.queries

import database.Structure

object PermissionQueries {
  val Insert = String.format(
    "INSERT INTO %s (%s, %s) VALUES (?, ?)",
    Structure.Permissions.Name,
    Structure.Permissions.Columns.Name,
    Structure.Permissions.Columns.Description)
}
