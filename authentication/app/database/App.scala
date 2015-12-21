package database

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

case class App(Id: Long,
               Name: String,
               Description: String)

class AppTableDef(tag: Tag) extends Table[App](tag, Structure.Apps.Name) {
  def Id = column[Long](Structure.Apps.Columns.Id, O.PrimaryKey, O.AutoInc)
  def Name = column[String](Structure.Apps.Columns.Name)
  def Description = column[String](Structure.Apps.Columns.Description)

  override def * = (Id, Name, Description) <>(App.tupled, App.unapply)

  def nameIndex = index(Structure.Apps.Columns.Name + "_IDX", Name, unique = true)
}

object Apps {
  val apps = TableQuery[AppTableDef]

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  def add(app: App): Future[Int] = {
    dbConfig.db.run(apps += app)
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(apps.filter(_.Id === id).delete)
  }

  def setDescription(id: Long, description: String): Future[Int] = {
    val q = for { a <- apps if a.Id === id } yield a.Description
    dbConfig.db.run(q.update(description))
  }

  def get(id: Long): Future[Option[App]] = {
    dbConfig.db.run(apps.filter(_.Id === id).result.headOption)
  }

  def get(name: String): Future[Option[App]] = {
    dbConfig.db.run(apps.filter(_.Name === name).result.headOption)
  }

  def listAll(): Future[Seq[App]] = {
    dbConfig.db.run(apps.result)
  }
}