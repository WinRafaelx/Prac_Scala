package repository

import models.Book
import db.tables.BookTable
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api._
import db.DatabaseConnection.db

object BookRepository {
  private val books = BookTable.table

  def getAllBooks(implicit ec: ExecutionContext): Future[List[Book]] = {
    db.run(books.result).map(_.toList)
  }

  def getBook(id: Int)(implicit ec: ExecutionContext): Future[Option[Book]] = {
    db.run(books.filter(_.id === id).result.headOption)
  }

  def getBookByTitle(title: String)(implicit ec: ExecutionContext): Future[Option[Book]] = {
    db.run(books.filter(_.title === title).result.headOption)
  }

  def createBook(book: Book)(implicit ec: ExecutionContext): Future[Book] = {
    val insertQuery = books returning books.map(_.id) into ((book, id) => book.copy(id = id))
    db.run(insertQuery += book)
  }

  def deleteBook(id: Int)(implicit ec: ExecutionContext): Future[Boolean] = {
    db.run(books.filter(_.id === id).delete).map(_ > 0)
  }

  def updateBook(id: Int, book: Book)(implicit ec: ExecutionContext): Future[Option[Book]] = {
    val updateBook = book.copy(id = id)
    val updateQuery = books.filter(_.id === id).update(updateBook)
    db.run(updateQuery).flatMap { 
      case 0 => Future.successful(None)
      case _ => Future.successful(Some(updateBook))
    }
  }
}