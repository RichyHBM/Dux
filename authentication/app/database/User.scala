package database

import java.util.Date

case class User(Id: Int,
  Name: String,
  Email: String,
  Password: List[Byte],
  Salt: List[Byte],
  ApiKey: String,
  CreatedOn: Date,
  FailedAttempts: Int,
  Blocked: Boolean)
