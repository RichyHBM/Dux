package database

object Structure {
  object Users {
    val Name = "Users"
    object Columns {
      val Id = "Id"
      val Name = "Name"
      val Email = "Email"
      val Password = "Password"
      val Salt = "Salt"
      val ApiKey = "ApiKey"
      val CreatedOn = "CreatedOn"
      val FailedAttempts = "FailedAttempts"
      val Blocked = "Blocked"
    }
  }

  object Groups {
    val Name = "Groups"
    object Columns {
      val Id = "Id"
      val Name = "Name"
      val Description = "Description"
    }
  }

  object Permissions {
    val Name = "Permissions"
    object Columns {
      val Id = "Id"
      val Name = "Name"
      val Description = "Description"
    }
  }

  object Apps {
    val Name = "Apps"
    object Columns {
      val Id = "Id"
      val Name = "Name"
      val Description = "Description"
    }
  }
}
