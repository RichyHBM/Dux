package database.queries

import database.Structure

object UserQueries {
  val Insert = String.format(
    "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
    Structure.Users.Name,
    Structure.Users.Columns.Name,
    Structure.Users.Columns.Email,
    Structure.Users.Columns.Password,
    Structure.Users.Columns.Salt,
    Structure.Users.Columns.ApiKey,
    Structure.Users.Columns.CreatedOn,
    Structure.Users.Columns.FailedAttempts,
    Structure.Users.Columns.Blocked)
}