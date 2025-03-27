package services

import domain.{Book}
import repositories.BookRepositoryTrait
import com.github.t3hnar.bcrypt._
import scala.concurrent.{ExecutionContext, Future}

class BookService(BookRepository: BookRepositoryTrait) {
  
  def createBook(book: Book)(implicit ec: ExecutionContext): Future[Book] = {
    BookRepository.createBook(book)
  }
  
  def findBookById(id: Long)(implicit ec: ExecutionContext): Future[Option[Book]] = {
    BookRepository.findBookById(id)
  }
  
  def findAllBooks()(implicit ec: ExecutionContext): Future[Seq[Book]] = {
    BookRepository.findAllBooks()
  }
  
  def deleteBook(id: Long)(implicit ec: ExecutionContext): Future[Boolean] = {
    BookRepository.deleteBook(id)
  }
  
  def updateBook(book: Book)(implicit ec: ExecutionContext): Future[Option[Book]] = {
    BookRepository.updateBook(book)
  }
}

object BookService {
  def apply(BookRepository: BookRepositoryTrait): BookService = new BookService(BookRepository)
}