package api.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.StatusCodes
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import services.BookService
import api.models.BookModel
import domain.Book
import scala.concurrent.ExecutionContext
import scala.util.{Success, Failure}

class BookRoutes(bookService: BookService)(implicit ec: ExecutionContext) {
  val routes: Route =
    pathPrefix("books") {
      concat(
        pathEndOrSingleSlash {
          concat(
            get {
              onComplete(bookService.findAllBooks()) {
                case Success(books) => complete(books)
                case Failure(ex) =>
                  complete(StatusCodes.InternalServerError -> ex.getMessage)
              }
            },
            post {
              entity(as[BookModel]) { bookModel =>
                val book = Book(
                  None,
                  bookModel.title,
                  bookModel.author,
                  bookModel.publishedDate
                )
                onComplete(bookService.createBook(book)) {
                  case Success(createdBook) =>
                    complete(StatusCodes.Created -> createdBook)
                  case Failure(ex) =>
                    complete(StatusCodes.InternalServerError -> ex.getMessage)
                }
              }
            }
          )
        },
        path(LongNumber) { id =>
          concat(
            get {
              onComplete(bookService.findBookById(id)) {
                case Success(Some(book)) => complete(book)
                case Success(None)       => complete(StatusCodes.NotFound)
                case Failure(ex) =>
                  complete(StatusCodes.InternalServerError -> ex.getMessage)
              }
            },
            put {
              entity(as[BookModel]) { bookModel =>
                val book = Book(
                  Some(id),
                  bookModel.title,
                  bookModel.author,
                  bookModel.publishedDate
                )
                onComplete(bookService.updateBook(book)) {
                  case Success(Some(updatedBook)) => complete(updatedBook)
                  case Success(None) => complete(StatusCodes.NotFound)
                  case Failure(ex) =>
                    complete(StatusCodes.InternalServerError -> ex.getMessage)
                }
              }
            },
            delete {
              onComplete(bookService.deleteBook(id)) {
                case Success(true)  => complete(StatusCodes.NoContent)
                case Success(false) => complete(StatusCodes.NotFound)
                case Failure(ex) =>
                  complete(StatusCodes.InternalServerError -> ex.getMessage)
              }
            }
          )
        }
      )
    }
}

object BookRoutes {
  def apply(bookService: BookService)(implicit
      ec: ExecutionContext
  ): BookRoutes = new BookRoutes(bookService)
}
