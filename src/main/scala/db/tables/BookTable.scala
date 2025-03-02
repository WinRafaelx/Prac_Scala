package db.tables

import models.Book
import slick.jdbc.PostgresProfile.api._

class BookTable(tag: Tag) extends Table[Book](tag, "books") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title")
  def author = column[String]("author")

  def * = (id, title, author).mapTo[Book]
}

// Companion object for table queries
object BookTable {
  val table = TableQuery[BookTable]
}
