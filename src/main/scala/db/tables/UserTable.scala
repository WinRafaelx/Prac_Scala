package db.tables

import models.User
import slick.jdbc.PostgresProfile.api._

class UserTable(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.Unique)
  def email = column[String]("email", O.Unique)
  def password = column[String]("password")
  
  def * = (id, name, email, password).mapTo[User]
}

object UserTable {
  val table = TableQuery[UserTable]
}