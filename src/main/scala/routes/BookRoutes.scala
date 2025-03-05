package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.StatusCodes
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import repository.BookRepository
import models.Book
import directives.AuthDirectives
import services.AuthService
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class BookRoutes(authDirectives: AuthDirectives)(implicit ec: ExecutionContext) {
  import authDirectives._
  
  val routes: Route =
    // Add authentication middleware to all book routes
    authenticated { userId =>
      pathPrefix("books") {
        concat(
          // Get all books
          pathEndOrSingleSlash {
            get {
              onComplete(BookRepository.getAllBooks) {
                case Success(books) => complete(books)
                case Failure(_)     => complete(StatusCodes.InternalServerError)
              }
            }
          },
          // Get a single book
          path(IntNumber) { id =>
            get {
              onComplete(BookRepository.getBook(id)) {
                case Success(Some(book)) => complete(book)
                case Success(None)       => complete(StatusCodes.NotFound)
                case Failure(_)          => complete(StatusCodes.InternalServerError)
              }
            }
          },
          // Get a single book by title
          path("title" / Segment) { title =>
            get {
              onComplete(BookRepository.getBookByTitle(title)) {
                case Success(Some(book)) => complete(book)
                case Success(None)       => complete(StatusCodes.NotFound)
                case Failure(_)          => complete(StatusCodes.InternalServerError)
              }
            }
          },
          // Create a book
          pathEndOrSingleSlash {
            post {
              entity(as[Book]) { book =>
                onComplete(BookRepository.createBook(book)) {
                  case Success(newBook) => complete(StatusCodes.Created, newBook)
                  case Failure(_)       => complete(StatusCodes.InternalServerError)
                }
              }
            }
          },
          // Delete a book
          path(IntNumber) { id =>
            delete {
              onComplete(BookRepository.deleteBook(id)) {
                case Success(true)  => complete(StatusCodes.OK)
                case Success(false) => complete(StatusCodes.NotFound)
                case Failure(_)     => complete(StatusCodes.InternalServerError)
              }
            }
          },
          // Update a book
          path(IntNumber) { id =>
            put {
              entity(as[Book]) { book =>
                onComplete(BookRepository.updateBook(id, book)) {
                  case Success(Some(updatedBook)) => complete(updatedBook)
                  case Success(None)              => complete(StatusCodes.NotFound)
                  case Failure(_)                 => complete(StatusCodes.InternalServerError)
                }
              }
            }
          }
        )
      }
    }
}

object BookRoutes {
  def apply(authDirectives: AuthDirectives)(implicit ec: ExecutionContext): BookRoutes = 
    new BookRoutes(authDirectives)
}