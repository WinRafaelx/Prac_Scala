package domain

import slick.jdbc.PostgresProfile.api._
import java.util.UUID
import java.sql.Timestamp

case class UserInfo(
  id: UUID,
  userAuthId: UUID,
  fullName: String,
  dateOfBirth: Option[java.sql.Date],
  passportNumber: Option[String],
  nationality: Option[String],
  address: Option[String],
  createdAt: Timestamp
)

class UserInfoTable(tag: Tag) extends Table[UserInfo](tag, "user_info") {
  def id = column[UUID]("id", O.PrimaryKey)
  def userAuthId = column[UUID]("user_auth_id", O.Unique)
  def fullName = column[String]("full_name")
  def dateOfBirth = column[Option[java.sql.Date]]("date_of_birth")
  def passportNumber = column[Option[String]]("passport_number", O.Unique)
  def nationality = column[Option[String]]("nationality")
  def address = column[Option[String]]("address")
  def createdAt = column[Timestamp]("created_at")

  def userAuthFk = foreignKey("fk_user_auth", userAuthId, UserAuthTable.table)(_.id)

  def * = (id, userAuthId, fullName, dateOfBirth, passportNumber, nationality, address, createdAt).mapTo[UserInfo]
}

object UserInfoTable {
  val table = TableQuery[UserInfoTable]
}