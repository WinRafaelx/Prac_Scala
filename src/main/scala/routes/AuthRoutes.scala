package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.StatusCodes
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import models.{LoginRequest, LoginResponse, RegisterRequest}
import repository.UserRepository
import services.JwtService
import com.github.t3hnar.bcrypt._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}

object AuthRoutes {
  val routes: Route =
    pathPrefix("auth") {
      concat(
        path("register") {
          post {
            entity(as[RegisterRequest]) { request =>
              val hashedPassword = request.password.bcrypt
              val user = models.User(None, request.name, request.email, hashedPassword)
              
              onComplete(UserRepository.createUser(user)) {
                case Success(createdUser) => complete(StatusCodes.Created)
                case Failure(_) => complete(StatusCodes.InternalServerError)
              }
            }
          }
        },
        path("login") {
          post {
            entity(as[LoginRequest]) { credentials =>
              onComplete(UserRepository.findByEmail(credentials.email)) {
                case Success(Some(user)) if credentials.password.isBcrypted(user.password) =>
                  val token = JwtService.createToken(user.id, user.email)
                  complete(LoginResponse(token))
                case _ => complete(StatusCodes.Unauthorized)
              }
            }
          }
        }
      )
    }
}