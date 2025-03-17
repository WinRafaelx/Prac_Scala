package api.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.StatusCodes
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import api.models.{UserInfoRequest, UserInfoResponse}
import services.UserInfoService
import api.directives.AuthDirectives
import scala.concurrent.ExecutionContext
import scala.util.{Success, Failure}
import java.util.UUID

class UserInfoRoutes(userInfoService: UserInfoService, authDirectives: AuthDirectives)(implicit ec: ExecutionContext) {
  
  import authDirectives._
  
  val routes: Route =
    pathPrefix("user-info") {
      authenticated { userAuthId =>
        concat(
          pathEndOrSingleSlash {
            concat(
              // POST /user-info - Create user info
              post {
                entity(as[UserInfoRequest]) { request =>
                  onComplete(userInfoService.createUserInfo(
                    userAuthId,
                    request.fullName,
                    request.dateOfBirth,
                    request.passportNumber,
                    request.nationality,
                    request.address
                  )) {
                    case Success(id) =>
                      complete(StatusCodes.Created -> Map("id" -> id.toString))
                    case Failure(ex) =>
                      complete(StatusCodes.InternalServerError -> ex.getMessage)
                  }
                }
              },
              
              // GET /user-info - Get current user's info
              get {
                onComplete(userInfoService.getUserInfoById(userAuthId)) {
                  case Success(Some(userInfo)) =>
                    complete(UserInfoResponse.fromDomain(userInfo))
                  case Success(None) =>
                    complete(StatusCodes.NotFound -> "User info not found")
                  case Failure(ex) =>
                    complete(StatusCodes.InternalServerError -> ex.getMessage)
                }
              }
            )
          },
          
          // Path with ID parameter
          path(JavaUUID) { id =>
            concat(
              // GET /user-info/{id} - Get specific user info
              get {
                onComplete(userInfoService.getUserInfoById(id)) {
                  case Success(Some(userInfo)) =>
                    // Check authorization - users can only view their own info
                    if (userInfo.userAuthId == userAuthId) {
                      complete(UserInfoResponse.fromDomain(userInfo))
                    } else {
                      complete(StatusCodes.Forbidden -> "Access denied")
                    }
                  case Success(None) =>
                    complete(StatusCodes.NotFound -> "User info not found")
                  case Failure(ex) =>
                    complete(StatusCodes.InternalServerError -> ex.getMessage)
                }
              },
              
              // PUT /user-info/{id} - Update specific user info
              put {
                entity(as[UserInfoRequest]) { request =>
                  // First check if user is authorized to update this record
                  onComplete(userInfoService.getUserInfoById(id)) {
                    case Success(Some(userInfo)) if userInfo.userAuthId == userAuthId =>
                      // User is authorized to update their own info
                      onComplete(userInfoService.updateUserInfo(
                        id,
                        Some(request.fullName),
                        request.dateOfBirth,
                        request.passportNumber,
                        request.nationality,
                        request.address
                      )) {
                        case Success(true) =>
                          complete(StatusCodes.OK -> "User info updated successfully")
                        case Success(false) =>
                          complete(StatusCodes.NotFound -> "User info not found")
                        case Failure(ex) =>
                          complete(StatusCodes.InternalServerError -> ex.getMessage)
                      }
                    case Success(Some(_)) =>
                      // User is not authorized to update someone else's info
                      complete(StatusCodes.Forbidden -> "Access denied")
                    case Success(None) =>
                      complete(StatusCodes.NotFound -> "User info not found")
                    case Failure(ex) =>
                      complete(StatusCodes.InternalServerError -> ex.getMessage)
                  }
                }
              },
              
              // DELETE /user-info/{id} - Delete specific user info
              delete {
                // First check if user is authorized to delete this record
                onComplete(userInfoService.getUserInfoById(id)) {
                  case Success(Some(userInfo)) if userInfo.userAuthId == userAuthId =>
                    // User is authorized to delete their own info
                    onComplete(userInfoService.deleteUserInfo(id)) {
                      case Success(true) =>
                        complete(StatusCodes.OK -> "User info deleted successfully")
                      case Success(false) =>
                        complete(StatusCodes.NotFound -> "User info not found")
                      case Failure(ex) =>
                        complete(StatusCodes.InternalServerError -> ex.getMessage)
                    }
                  case Success(Some(_)) =>
                    // User is not authorized to delete someone else's info
                    complete(StatusCodes.Forbidden -> "Access denied")
                  case Success(None) =>
                    complete(StatusCodes.NotFound -> "User info not found")
                  case Failure(ex) =>
                    complete(StatusCodes.InternalServerError -> ex.getMessage)
                }
              }
            )
          }
        )
      }
    }
}

object UserInfoRoutes {
  def apply(userInfoService: UserInfoService, authDirectives: AuthDirectives)(implicit ec: ExecutionContext): UserInfoRoutes =
    new UserInfoRoutes(userInfoService, authDirectives)
}