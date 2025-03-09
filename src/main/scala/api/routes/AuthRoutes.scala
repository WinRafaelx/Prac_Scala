package api.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.StatusCodes
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import api.models.{LoginRequest, LoginResponse, RegisterRequest}
import services.AuthService
import scala.concurrent.ExecutionContext
import scala.util.{Success, Failure}

class AuthRoutes(authService: AuthService)(implicit ec: ExecutionContext) {
  val routes: Route =
    pathPrefix("auth") {
      concat(
        path("register") {
          post {
            entity(as[RegisterRequest]) { request =>
              onComplete(authService.register(request)) {
                case Success(Right((token, _))) => 
                  complete(StatusCodes.Created -> LoginResponse(token))
                case Success(Left(error)) =>
                  complete(StatusCodes.BadRequest -> error)
                case Failure(ex) => 
                  complete(StatusCodes.InternalServerError -> ex.getMessage)
              }
            }
          }
        },
        path("login") {
          post {
            entity(as[LoginRequest]) { credentials =>
              onComplete(authService.login(credentials)) {
                case Success(Right(token)) => 
                  complete(LoginResponse(token))
                case Success(Left(error)) =>
                  complete(StatusCodes.Unauthorized -> error)
                case Failure(ex) =>
                  complete(StatusCodes.InternalServerError -> ex.getMessage)
              }
            }
          }
        }
      )
    }
}

object AuthRoutes {
  def apply(authService: AuthService)(implicit ec: ExecutionContext): AuthRoutes = 
    new AuthRoutes(authService)
}