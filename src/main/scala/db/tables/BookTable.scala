package db.tables

import models.Book
import slick.jdbc.PostgresProfile.api._

/** Defines the 'books' table schema */
class BookTable(tag: Tag) extends Table[Book](tag, "books") {

  /** ID (Primary Key, Auto-Increment) */
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  /** Book title (Unique) */
  def title = column[String]("title", O.Unique)

  /** Author name */
  def author = column[String]("author")

  /** Table mapping */
  def * = (id.?, title, author).mapTo[Book]
}

object BookTable {
  val table = TableQuery[BookTable]

  /** Fetches a book by ID */
  def findById(id: Int) = table.filter(_.id === id)
}