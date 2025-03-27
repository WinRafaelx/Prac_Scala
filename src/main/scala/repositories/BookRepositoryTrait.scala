package repositories

import domain.Book
import api.models.BookModel
import scala.concurrent.{ExecutionContext, Future}

trait BookRepositoryTrait {
  def createBook(book: Book)(implicit ec: ExecutionContext): Future[Book]
  def findBookById(id: Long)(implicit ec: ExecutionContext): Future[Option[Book]]
  def findAllBooks()(implicit ec: ExecutionContext): Future[Seq[Book]]
  def updateBook(book: Book)(implicit ec: ExecutionContext): Future[Option[Book]]
  def deleteBook(id: Long)(implicit ec: ExecutionContext): Future[Boolean]
}