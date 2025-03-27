package repositories

import domain.Book
import api.models.{BookModel, BookTable}
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api._
import db.DatabaseConnection.db
import org.slf4j.LoggerFactory
import java.sql.Timestamp
import java.time.Instant

object BookRepository extends BookRepositoryTrait {
  private val books = BookTable.query
  private val logger = LoggerFactory.getLogger(this.getClass)

  def createBook(book: Book)(implicit ec: ExecutionContext): Future[Book] = {
    val bookModel = Book.toModel(book)
    db.run((books returning books.map(_.id)) += bookModel)
      .map(id => Book.fromModel(bookModel.copy(id = id)))
      .recoverWith { case e =>
        logger.error("Failed to create book", e)
        Future.failed(e)
      }
  }

  def findBookById(
      id: Long
  )(implicit ec: ExecutionContext): Future[Option[Book]] = {
    db.run(books.filter(_.id === id).result.headOption)
      .map(_.map(Book.fromModel))
      .recoverWith { case e =>
        logger.error("Failed to find book by ID", e)
        Future.failed(e)
      }
  }

  def findAllBooks()(implicit ec: ExecutionContext): Future[Seq[Book]] = {
    db.run(books.result)
      .map(_.map(Book.fromModel))
      .recoverWith { case e =>
        logger.error("Failed to find all books", e)
        Future.failed(e)
      }
  }

  def deleteBook(id: Long)(implicit ec: ExecutionContext): Future[Boolean] = {
    db.run(books.filter(_.id === id).delete)
      .map(_ > 0)
      .recoverWith { case e =>
        logger.error("Failed to delete book", e)
        Future.failed(e)
      }
  }

  def updateBook(
      book: Book
  )(implicit ec: ExecutionContext): Future[Option[Book]] = {
    val bookModel = Book.toModel(book)
    db.run(books.filter(_.id === book.id).update(bookModel))
      .map {
        case 1 => Some(Book.fromModel(bookModel))
        case _ => None
      }
      .recoverWith { case e =>
        logger.error("Failed to update book", e)
        Future.failed(e)
      }
  }
}
