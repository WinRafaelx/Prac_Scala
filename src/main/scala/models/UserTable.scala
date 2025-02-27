package models

import slick.jdbc.PostgresProfile.api._

class UserTable(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Int]("id", O.PrimaryKey)
  def name = column[String]("name")
  def email = column[String]("email")
  
  def * = (id, name, email).mapTo[User]
}