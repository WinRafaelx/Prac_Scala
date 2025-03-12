package domain

import slick.jdbc.PostgresProfile.api._
import java.util.UUID
import java.sql.Timestamp

case class Itinerary(
  id: UUID,
  userAuthId: UUID,
  status: String,
  totalPrice: BigDecimal,
  createdAt: Timestamp
)

class ItineraryTable(tag: Tag) extends Table[Itinerary](tag, "itineraries") {
  def id = column[UUID]("id", O.PrimaryKey)
  def userAuthId = column[UUID]("user_auth_id")
  def status = column[String]("status", O.Default("draft"))
  def totalPrice = column[BigDecimal]("total_price")
  def createdAt = column[Timestamp]("created_at")

  def userAuthFk = foreignKey("fk_user_auth", userAuthId, UserAuthTable.table)(_.id)

  def * = (id, userAuthId, status, totalPrice, createdAt).mapTo[Itinerary]
}

object ItineraryTable {
  val table = TableQuery[ItineraryTable]
}