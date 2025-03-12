package domain

import slick.jdbc.PostgresProfile.api._
import java.util.UUID
import java.sql.Timestamp

case class Flight(
  id: UUID,
  airlineId: UUID,
  flightNumber: String,
  originAirportId: UUID,
  destinationAirportId: UUID,
  departureTime: Timestamp,
  arrivalTime: Timestamp,
  duration: Long,
  totalSeats: Int,
  availableSeats: Int,
  basePrice: BigDecimal,
  createdAt: Timestamp
)

class FlightTable(tag: Tag) extends Table[Flight](tag, "flights") {
  def id = column[UUID]("id", O.PrimaryKey)
  def airlineId = column[UUID]("airline_id")
  def flightNumber = column[String]("flight_number", O.Unique)
  def originAirportId = column[UUID]("origin_airport_id")
  def destinationAirportId = column[UUID]("destination_airport_id")
  def departureTime = column[Timestamp]("departure_time")
  def arrivalTime = column[Timestamp]("arrival_time")
  def duration = column[Long]("duration")
  def totalSeats = column[Int]("total_seats")
  def availableSeats = column[Int]("available_seats")
  def basePrice = column[BigDecimal]("base_price")
  def createdAt = column[Timestamp]("created_at")

  def airlineFk = foreignKey("fk_airline", airlineId, AirlineTable.table)(_.id)
  def originAirportFk = foreignKey("fk_origin_airport", originAirportId, AirportTable.table)(_.id)
  def destinationAirportFk = foreignKey("fk_destination_airport", destinationAirportId, AirportTable.table)(_.id)

  def * = (
    id, airlineId, flightNumber, originAirportId, destinationAirportId,
    departureTime, arrivalTime, duration, totalSeats, availableSeats,
    basePrice, createdAt
  ).mapTo[Flight]
}

object FlightTable {
  val table = TableQuery[FlightTable]
}