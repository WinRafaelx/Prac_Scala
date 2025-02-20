package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.StatusCodes
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import repository.UserRepository
import models.User
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object UserRoutes {
  val routes: Route =
    pathPrefix("users") {
      concat(
        // Get all users
        pathEndOrSingleSlash {
          get {
            onComplete(UserRepository.getAllUsers) {
              case Success(users) => complete(users)
              case Failure(_)     => complete(StatusCodes.InternalServerError)
            }
          }
        },
        // Get a single user
        path(IntNumber) { id =>
          get {
            onComplete(UserRepository.getUser(id)) {
              case Success(Some(user)) => complete(user)
              case Success(None)       => complete(StatusCodes.NotFound)
              case Failure(_)          => complete(StatusCodes.InternalServerError)
            }
          }
        },
        // Create a user
        pathEndOrSingleSlash {
          post {
            entity(as[User]) { user =>
              onComplete(UserRepository.createUser(user)) {
                case Success(newUser) => complete(StatusCodes.Created, newUser)
                case Failure(_)       => complete(StatusCodes.InternalServerError)
              }
            }
          }
        },
        // Delete a user
        path(IntNumber) { id =>
          delete {
            onComplete(UserRepository.deleteUser(id)) {
              case Success(true)  => complete(StatusCodes.OK)
              case Success(false) => complete(StatusCodes.NotFound)
              case Failure(_)     => complete(StatusCodes.InternalServerError)
            }
          }
        },
        // Update a user
        path(IntNumber) { id =>
          put {
            entity(as[User]) { user =>
              onComplete(UserRepository.updateUser(id, user)) {
                case Success(Some(updatedUser)) => complete(updatedUser)
                case Success(None)              => complete(StatusCodes.NotFound)
                case Failure(_)                 => complete(StatusCodes.InternalServerError)
              }
            }
          }
        },
        // Update a user's email
        path(IntNumber / "email") { id =>
          put {
            entity(as[String]) { email =>
              onComplete(UserRepository.updateUserEmail(id, email)) {
                case Success(Some(updatedUser)) => complete(updatedUser)
                case Success(None)              => complete(StatusCodes.NotFound)
                case Failure(_)                 => complete(StatusCodes.InternalServerError)
              }
            }
          }
        }
      )
    }
}