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

  object UserGroup {
    val Name = "UsersGroups"
    object Columns {
      val Id = "Id"
      val UserId = "UserId"
      val GroupId = "GroupId"
    }
  }

  object GroupPermission {
    val Name = "GroupsPermissions"
    object Columns {
      val Id = "Id"
      val GroupId = "GroupId"
      val PermissionId = "PermissionId"
    }
  }

  object PermissionApp {
    val Name = "PermissionsApps"
    object Columns {
      val Id = "Id"
      val PermissionId = "PermissionId"
      val AppId = "AppId"
    }
  }
}
