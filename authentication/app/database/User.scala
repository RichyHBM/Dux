package database

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import java.sql.Timestamp
import scala.concurrent.ExecutionContext.Implicits.global

case class User(Id: Long,
  Name: String,
  Email: String,
  Password: Array[Byte],
  Salt: Array[Byte],
  ApiKey: String,
  CreatedOn: Timestamp,
  FailedAttempts: Int,
  Blocked: Boolean)

class UserTableDef(tag: Tag) extends Table[User](tag, Structure.Users.Name) {
  def Id = column[Long](Structure.Users.Columns.Id, O.PrimaryKey, O.AutoInc)
  def Name = column[String](Structure.Users.Columns.Name)
  def Email = column[String](Structure.Users.Columns.Email)
  def Password = column[Array[Byte]](Structure.Users.Columns.Password)
  def Salt = column[Array[Byte]](Structure.Users.Columns.Salt)
  def ApiKey = column[String](Structure.Users.Columns.ApiKey)
  def CreatedOn = column[Timestamp](Structure.Users.Columns.CreatedOn)
  def FailedAttempts = column[Int](Structure.Users.Columns.FailedAttempts, O.Default(0))
  def Blocked = column[Boolean](Structure.Users.Columns.Blocked, O.Default(false))

  override def * = (Id, Name, Email, Password, Salt, ApiKey, CreatedOn, FailedAttempts, Blocked) <>(User.tupled, User.unapply)

  def emailIndex = index(Structure.Users.Columns.Email + "_IDX", Email, unique = true)
  def apiKeyIndex = index(Structure.Users.Columns.ApiKey + "_IDX", ApiKey, unique = true)
}

object Users {
  val users = TableQuery[UserTableDef]

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  def add(user: User): Future[Option[Exception]] = {
    dbConfig.db.run(users += user).map(res => None).recover {
      case ex: Exception => Some(ex)
    }
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(users.filter(_.Id === id).delete)
  }

  def get(id: Long): Future[Option[User]] = {
    dbConfig.db.run(users.filter(_.Id === id).result.headOption)
  }

  def listAll(): Future[Seq[User]] = {
    dbConfig.db.run(users.result)
  }
}
