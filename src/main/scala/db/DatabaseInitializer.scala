package db

import models.{BookTable, UserTable}
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ExecutionContext, Future}

object DatabaseInitializer {
  private val users = TableQuery[UserTable]
  private val books = TableQuery[BookTable]

  def initDatabase()(implicit ec: ExecutionContext): Future[Unit] = {
    val db = DatabaseConnection.db
    
    println("Checking database tables...")
    
    // Create schemas with better error handling
    val setup = DBIO.seq(
      // Create tables if they don't exist
      sqlu"CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, name VARCHAR(255), email VARCHAR(255))",
      sqlu"CREATE TABLE IF NOT EXISTS books (id INTEGER PRIMARY KEY, title VARCHAR(255), author VARCHAR(255))"
    ).transactionally

    db.run(setup).recoverWith { case ex =>
      println(s"Error during table creation: ${ex.getMessage}")
      Future.failed(ex)
    }
  }
}