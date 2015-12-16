package database.queries

import database.Structure

object AppQueries {
  val Insert = String.format(
    "INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)",
    Structure.Apps.Name,
    Structure.Apps.Columns.AppIdentifier,
    Structure.Apps.Columns.FullName,
    Structure.Apps.Columns.Description)
}
