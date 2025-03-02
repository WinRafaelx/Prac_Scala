package db

import db.tables.{BookTable, UserTable}
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ExecutionContext, Future}

object DatabaseInitializer {
  private val books = BookTable.table
  private val users = UserTable.table

  def initDatabase()(implicit ec: ExecutionContext): Future[Unit] = {
    val db = DatabaseConnection.db
    
    println("Initializing database schemas...")
    
    val createSchemas = DBIO.seq(
      books.schema.createIfNotExists,
      users.schema.createIfNotExists
    ).transactionally

    db.run(createSchemas).map { _ =>
      println("Database schemas created successfully")
    }.recover { case ex =>
      println(s"Failed to create database schemas: ${ex.getMessage}")
      throw ex
    }
  }
}