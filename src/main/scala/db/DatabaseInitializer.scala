package db

import db.tables.{BookTable, UserTable}
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ExecutionContext, Future}

object DatabaseInitializer {
  private val books = BookTable.table
  private val users = UserTable.table

  def initDatabase()(implicit ec: ExecutionContext): Future[Unit] = {
    val db = DatabaseConnection.db
    
    println("Checking database connection...")
    
    val createSchemas = DBIO.seq(
      // Create tables using raw SQL first to ensure they exist
      sqlu"""CREATE TABLE IF NOT EXISTS users (
        id SERIAL PRIMARY KEY,
        name VARCHAR(255) UNIQUE NOT NULL,
        email VARCHAR(255) UNIQUE NOT NULL,
        password VARCHAR(255) NOT NULL
      )""",
      
      sqlu"""CREATE TABLE IF NOT EXISTS books (
        id SERIAL PRIMARY KEY,
        title VARCHAR(255) NOT NULL,
        author VARCHAR(255) NOT NULL
      )""",
      
      // Then create/update schema using Slick to ensure all mappings are correct
      books.schema.createIfNotExists,
      users.schema.createIfNotExists
    ).transactionally

    db.run(createSchemas).map { _ =>
      println("Database schemas created/updated successfully")
    }.recoverWith { case ex => 
      println(s"""
        |Failed to create database schemas: ${ex.getMessage}
        |Cause: ${ex.getCause}
        |Stack trace: ${ex.getStackTrace.mkString("\n")}
        """.stripMargin)
      Future.failed(ex)
    }
  }
}