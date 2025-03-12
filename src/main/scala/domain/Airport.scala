package domain

import slick.jdbc.PostgresProfile.api._
import java.util.UUID
import java.sql.Timestamp

case class Airport(
  id: UUID,
  name: String,
  code: String,
  city: String,
  country: String
)

class AirportTable(tag: Tag) extends Table[Airport](tag, "airports") {
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[String]("name")
  def code = column[String]("code", O.Unique)
  def city = column[String]("city")
  def country = column[String]("country")

  def * = (id, name, code, city, country).mapTo[Airport]
}

object AirportTable {
  val table = TableQuery[AirportTable]
}