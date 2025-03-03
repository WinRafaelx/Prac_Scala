package db.tables

import models.User
import slick.jdbc.PostgresProfile.api._

/** Defines the 'users' table schema */
class UserTable(tag: Tag) extends Table[User](tag, "users") {

  /** ID (Primary Key, Auto-Increment) */
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  /** Username (Unique & Not Null) */
  def name = column[String]("name", O.Unique)

  /** Email (Unique & Not Null) */
  def email = column[String]("email", O.Unique)

  /** Password (Not Null) */
  def password = column[String]("password")

  /** Table mapping */
  def * = (id.?, name, email, password).mapTo[User]
}

object UserTable {
  val table = TableQuery[UserTable]
}