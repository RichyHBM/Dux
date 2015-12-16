package database.queries

import database.Structure

object GroupQueries {
  val Insert = String.format(
    "INSERT INTO %s (%s, %s) VALUES (?, ?)",
    Structure.Groups.Name,
    Structure.Groups.Columns.Name,
    Structure.Groups.Columns.Description)
}
