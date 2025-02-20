package repository

import models.Book
import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object BookRepository {
    private val books: mutable.Map[Int, Book] = mutable.Map()

    def getAllBooks: Future[List[Book]] = Future {
        books.values.toList
    }

    def getBook(id: Int): Future[Option[Book]] = Future {
        books.get(id)
    }

    def getBookByTitle(title: String): Future[Option[Book]] = Future {
        books.values.find(_.title == title)
    }

    def createBook(book: Book): Future[Book] = Future {
        // Check if the book already exists
        if (books.values.exists(_.title == book.title)) {
            throw new Exception("Book already exists")
        }

        books += (book.id -> book)
        book
    }

    def deleteBook(id: Int): Future[Boolean] = Future {
        books.remove(id).isDefined
    }

    def updateBook(id: Int, book: Book): Future[Option[Book]] = Future {
        books.get(id) match {
            case Some(_) =>
                books.update(id, book)
                Some(book)
            case None => None
        }
    }
}