package directives

import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.model.StatusCodes
import services.AuthService
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Failure}

class AuthDirectives(authService: AuthService)(implicit ec: ExecutionContext) {

  /**
   * Directive to authenticate requests based on JWT token
   * @return Directive with authenticated user ID
   */
  def authenticated: Directive1[Int] = {
    extractCredentials.flatMap {
      case Some(OAuth2BearerToken(token)) =>
        onComplete(authService.validateToken(token)).flatMap {
          case Success(Some(userId)) => provide(userId)
          case _ => complete(StatusCodes.Unauthorized -> "Invalid authentication token")
        }
      case _ => complete(StatusCodes.Unauthorized -> "Authentication required")
    }
  }
  
  /**
   * Check if user has admin role
   */
  def hasAdminRole(userId: Int): Directive1[Int] = {
    // In a real app, you'd check if the user has admin role
    // For now we just pass through
    provide(userId)
  }
}

object AuthDirectives {
  def apply(authService: AuthService)(implicit ec: ExecutionContext): AuthDirectives = 
    new AuthDirectives(authService)
}