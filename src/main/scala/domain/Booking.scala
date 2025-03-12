package domain

import slick.jdbc.PostgresProfile.api._
import java.util.UUID
import java.sql.Timestamp

case class Booking(
  id: UUID,
  itineraryId: UUID,
  flightId: UUID,
  seatId: UUID,
  bookingStatus: String,
  createdAt: Timestamp
)

class BookingTable(tag: Tag) extends Table[Booking](tag, "bookings") {
  def id = column[UUID]("id", O.PrimaryKey)
  def itineraryId = column[UUID]("itinerary_id")
  def flightId = column[UUID]("flight_id")
  def seatId = column[UUID]("seat_id")
  def bookingStatus = column[String]("booking_status", O.Default("pending"))
  def createdAt = column[Timestamp]("created_at")

  def itineraryFk = foreignKey("fk_itinerary", itineraryId, ItineraryTable.table)(_.id)
  def flightFk = foreignKey("fk_flight", flightId, FlightTable.table)(_.id)
  def seatFk = foreignKey("fk_seat", seatId, SeatTable.table)(_.id)

  def * = (id, itineraryId, flightId, seatId, bookingStatus, createdAt).mapTo[Booking]
}

object BookingTable {
  val table = TableQuery[BookingTable]
}