package domain

import slick.jdbc.PostgresProfile.api._
import java.util.UUID
import java.sql.Timestamp

case class Seat(
  id: UUID,
  flightId: UUID,
  seatNumber: String,
  seatType: String,
  price: BigDecimal
)

class SeatTable(tag: Tag) extends Table[Seat](tag, "seats") {
  def id = column[UUID]("id", O.PrimaryKey)
  def flightId = column[UUID]("flight_id")
  def seatNumber = column[String]("seat_number")
  def seatType = column[String]("seat_type", O.Default("economy"))
  def price = column[BigDecimal]("price")

  def flightFk = foreignKey("fk_flight", flightId, FlightTable.table)(_.id)

  def * = (id, flightId, seatNumber, seatType, price).mapTo[Seat]
}

object SeatTable {
  val table = TableQuery[SeatTable]
}