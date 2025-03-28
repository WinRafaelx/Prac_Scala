package domain

import java.time.LocalDate
import api.models.BookModel
import java.sql.Timestamp

case class Book(
    id: Option[Long],
    title: String,
    author: String,
    publishedDate: LocalDate,
    createdAt: Timestamp
)

case class CreateBookRequest(
  title: String,
  author: String, 
  publishedDate: LocalDate  
)

case class UpdateBookRequest(
  title: Option[String],
  author: Option[String],
  publishedDate: Option[LocalDate]
)

object Book {

  // Convert a BookModel to a Book
  def fromModel(model: BookModel): Book =
    Book(
      Some(model.id),
      model.title,
      model.author,
      model.publishedDate,
      model.createdAt
    )

  // Convert a Book to a BookModel
  def toModel(domain: Book): BookModel =
    BookModel(
      domain.id.getOrElse(0L),
      domain.title,
      domain.author,
      domain.publishedDate,
      domain.createdAt
    )
}
