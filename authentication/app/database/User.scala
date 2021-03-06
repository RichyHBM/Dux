package database

import java.util.Date

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
  Blocked: Boolean) {

  def this(Name: String,
           Email: String,
           Password: Array[Byte],
           Salt: Array[Byte],
           ApiKey: String) = this(0, Name, Email, Password, Salt, ApiKey, new Timestamp(new Date().getTime()), 0, false)
}

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

  def add(user: User): Future[Int] = {
    dbConfig.db.run(users += user)
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(users.filter(_.Id === id).delete)
  }

  def updateUser(id: Long, user: User): Future[Int] = {
    val q = for { u <- users if u.Id === id } yield u
    dbConfig.db.run(q.update(user))
  }

  def setBlock(id: Long, blocked: Boolean): Future[Int] = {
    val q = for { u <- users if u.Id === id } yield u.Blocked
    dbConfig.db.run(q.update(blocked))
  }

  def get(id: Long): Future[Option[User]] = {
    dbConfig.db.run(users.filter(_.Id === id).result.headOption)
  }

  def getFromEmail(email: String): Future[Option[User]] = {
    dbConfig.db.run(users.filter(_.Email === email).result.headOption)
  }

  def getFromApiKey(apiKey: String): Future[Option[User]] = {
    dbConfig.db.run(users.filter(_.ApiKey === apiKey).result.headOption)
  }

  def get(ids: List[Long]): Future[Seq[User]] = {
    dbConfig.db.run(users.filter(_.Id.inSet(ids)).result)
  }

  def listAll(): Future[Seq[User]] = {
    dbConfig.db.run(users.result)
  }
}
