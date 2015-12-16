package database

import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

case class App(Id: Long,
               AppIdentifier: String,
               FullName: String,
               Description: String)

class AppTableDef(tag: Tag) extends Table[App](tag, Structure.Apps.Name) {
  def Id = column[Long](Structure.Apps.Columns.Id, O.PrimaryKey, O.AutoInc)
  def AppIdentifier = column[String](Structure.Apps.Columns.AppIdentifier)
  def FullName = column[String](Structure.Apps.Columns.FullName)
  def Description = column[String](Structure.Apps.Columns.Description)

  override def * = (Id, AppIdentifier, FullName, Description) <>(App.tupled, App.unapply)

  def appIdentifierIndex = index(Structure.Apps.Columns.AppIdentifier + "_IDX", AppIdentifier, unique = true)
}

object Apps {
  val apps = TableQuery[AppTableDef]

  def add(dbConfigProvider: DatabaseConfigProvider, app: App): Future[Option[Exception]] = {
    val dbConfig = dbConfigProvider.get[JdbcProfile]
    dbConfig.db.run(apps += app).map(res => None).recover {
      case ex: Exception => Some(ex)
    }
  }

  def delete(dbConfigProvider: DatabaseConfigProvider, id: Long): Future[Int] = {
    val dbConfig = dbConfigProvider.get[JdbcProfile]
    dbConfig.db.run(apps.filter(_.Id === id).delete)
  }

  def get(dbConfigProvider: DatabaseConfigProvider, id: Long): Future[Option[App]] = {
    val dbConfig = dbConfigProvider.get[JdbcProfile]
    dbConfig.db.run(apps.filter(_.Id === id).result.headOption)
  }

  def listAll(dbConfigProvider: DatabaseConfigProvider): Future[Seq[App]] = {
    val dbConfig = dbConfigProvider.get[JdbcProfile]
    dbConfig.db.run(apps.result)
  }
}
