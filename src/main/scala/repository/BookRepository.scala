package repository

import models.{Book, BookTable}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api._
import db.DatabaseConnection.db

object BookRepository {
  private val books = TableQuery[BookTable]

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
    db.run(books += book).map(_ => book)
  }

  def deleteBook(id: Int)(implicit ec: ExecutionContext): Future[Boolean] = {
    db.run(books.filter(_.id === id).delete).map(_ > 0)
  }

  def updateBook(id: Int, book: Book)(implicit ec: ExecutionContext): Future[Option[Book]] = {
    val updateQuery = books.filter(_.id === id).update(book)
    db.run(updateQuery).flatMap { rowsAffected =>
      if (rowsAffected > 0) Future.successful(Some(book))
      else Future.successful(None)
    }
  }
}