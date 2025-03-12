package domain

import slick.jdbc.PostgresProfile.api._
import java.util.UUID
import java.sql.Timestamp

case class Airline(
  id: UUID,
  name: String,
  code: String,
  logoUrl: Option[String],
  createdAt: Timestamp
)

class AirlineTable(tag: Tag) extends Table[Airline](tag, "airlines") {
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[String]("name", O.Unique)
  def code = column[String]("code", O.Unique)
  def logoUrl = column[Option[String]]("logo_url")
  def createdAt = column[Timestamp]("created_at")

  def * = (id, name, code, logoUrl, createdAt).mapTo[Airline]
}

object AirlineTable {
  val table = TableQuery[AirlineTable]
}