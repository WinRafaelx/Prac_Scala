package db.tables

import models.User
import slick.jdbc.PostgresProfile.api._

class UserTable(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def email = column[String]("email")
  
  def * = (id, name, email).mapTo[User]
}

object UserTable {
  val table = TableQuery[UserTable]
}
