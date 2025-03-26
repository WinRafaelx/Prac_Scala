package domain

import slick.jdbc.PostgresProfile.api._
import java.sql.Timestamp

case class UserAuth(
  id: Int,
  email: String,
  phone: Option[String],
  passwordHash: String,
  authProvider: String = "email",
  role: String = "user",
  createdAt: Timestamp
)

class UserAuthTable(tag: Tag) extends Table[UserAuth](tag, "user_auth") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def email = column[String]("email", O.Unique)
  def phone = column[Option[String]]("phone", O.Unique)
  def passwordHash = column[String]("password_hash")
  def authProvider = column[String]("auth_provider", O.Default("email"))
  def role = column[String]("role", O.Default("user"))
  def createdAt = column[Timestamp]("created_at")

  def * = (id, email, phone, passwordHash, authProvider, role, createdAt).mapTo[UserAuth]
}

object UserAuthTable {
  val table = TableQuery[UserAuthTable]
}
