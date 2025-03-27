package api.models

import slick.jdbc.PostgresProfile.api._

import java.time.LocalDate

case class BookModel(
    id: Long,
    title: String,
    author: String,
    publishedDate: LocalDate,
    createdAt: LocalDate = LocalDate.now()
)

class BookTable(tag: Tag) extends Table[BookModel](tag, "books") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title")
  def author = column[String]("author")
  def publishedDate = column[LocalDate]("published_date")
  def createdAt = column[LocalDate]("created_at")

  def * = (id, title, author, publishedDate, createdAt).mapTo[BookModel]
}

object BookTable {
  val query = TableQuery[BookTable]
}
