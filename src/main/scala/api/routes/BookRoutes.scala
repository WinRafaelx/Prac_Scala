package api.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.StatusCodes
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.circe.{Encoder, Decoder}
import io.circe.syntax._
import services.BookService
import domain.{Book, CreateBookRequest, UpdateBookRequest}
import scala.concurrent.ExecutionContext
import scala.util.{Success, Failure}
import java.sql.Timestamp
import java.time.{Instant, LocalDate}

class BookRoutes(bookService: BookService)(implicit ec: ExecutionContext) {
  
  // Custom encoders and decoders for Java types
  implicit val timestampEncoder: Encoder[Timestamp] = Encoder.encodeString.contramap[Timestamp](ts => 
    ts.toInstant.toString
  )
  
  implicit val timestampDecoder: Decoder[Timestamp] = Decoder.decodeString.map(str => 
    Timestamp.from(Instant.parse(str))
  )
  
  // Now the routes
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
              entity(as[CreateBookRequest]) { request =>
                val book = Book(
                  None,
                  request.title,
                  request.author,
                  request.publishedDate,
                  new Timestamp(System.currentTimeMillis())
                )
                onComplete(bookService.createBook(book)) {
                  case Success(createdBook) => complete(StatusCodes.Created -> createdBook)
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
                case Success(None) => complete(StatusCodes.NotFound)
                case Failure(ex) =>
                  complete(StatusCodes.InternalServerError -> ex.getMessage)
              }
            },
            put {
              entity(as[UpdateBookRequest]) { request =>
                onComplete(bookService.findBookById(id)) {
                  case Success(Some(book)) =>
                    val updatedBook = book.copy(
                      title = request.title.getOrElse(book.title),
                      author = request.author.getOrElse(book.author),
                      publishedDate = request.publishedDate.getOrElse(book.publishedDate)
                    )
                    onComplete(bookService.updateBook(updatedBook)) {
                      case Success(Some(book)) => complete(book)
                      case Success(None) => complete(StatusCodes.NotFound)
                      case Failure(ex) =>
                        complete(StatusCodes.InternalServerError -> ex.getMessage)
                    }
                  case Success(None) => complete(StatusCodes.NotFound)
                  case Failure(ex) =>
                    complete(StatusCodes.InternalServerError -> ex.getMessage)
                }
              }
            },
            delete {
              onComplete(bookService.deleteBook(id)) {
                case Success(true) => complete(StatusCodes.NoContent)
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