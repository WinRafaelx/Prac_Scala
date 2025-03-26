package db

import api.models.{UserAuthTable}
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ExecutionContext, Future}

object DatabaseInitializer {
  private val db = DatabaseConnection.db
  private val users = UserAuthTable.table

  /** Initialize database schemas */
  def initDatabase()(implicit ec: ExecutionContext): Future[Unit] = {
    println("Initializing database schemas...")

    val createSchemas = DBIO.seq(
      users.schema.createIfNotExists
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
