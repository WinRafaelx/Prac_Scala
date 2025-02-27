package db

import slick.jdbc.PostgresProfile.api._
import com.typesafe.config.ConfigFactory

object DatabaseConnection {
  private val config = ConfigFactory.load()
  val db = Database.forConfig("database.postgres")
}