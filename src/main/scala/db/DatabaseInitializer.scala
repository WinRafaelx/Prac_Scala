package db

import domain._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ExecutionContext, Future}

object DatabaseInitializer {
  private val db = DatabaseConnection.db
  
  // All domain table definitions
  private val userAuthTable = UserAuthTable.table
  private val userInfoTable = UserInfoTable.table
  private val airlineTable = AirlineTable.table
  private val airportTable = AirportTable.table
  private val flightTable = FlightTable.table
  private val seatTable = SeatTable.table
  private val bookingTable = BookingTable.table
  private val itineraryTable = ItineraryTable.table // This seems referenced but not defined in your files

  /** Initialize database schemas */
  def initDatabase()(implicit ec: ExecutionContext): Future[Unit] = {
    println("Initializing database schemas...")

    // Create schemas in order respecting foreign key dependencies
    val createSchemas = DBIO.seq(
      // First, tables with no dependencies
      userAuthTable.schema.createIfNotExists,
      airlineTable.schema.createIfNotExists,
      airportTable.schema.createIfNotExists,
      
      // Then tables with dependencies
      userInfoTable.schema.createIfNotExists,
      flightTable.schema.createIfNotExists,
      
      // Finally tables with multiple dependencies
      seatTable.schema.createIfNotExists,
      itineraryTable.schema.createIfNotExists,
      bookingTable.schema.createIfNotExists
    ).transactionally

    db.run(createSchemas)
      .map(_ => println("✅ Database schemas created/updated successfully"))
      .recoverWith { case ex =>
        println(
          s"""
             |❌ Failed to create database schemas:
             |  - Error: ${ex.getMessage}
             |  - Cause: ${Option(ex.getCause).map(_.toString).getOrElse("Unknown")}
             |  - Stack Trace: ${ex.getStackTrace.mkString("\n")}
             |""".stripMargin
        )
        Future.failed(ex)
      }
  }
}
