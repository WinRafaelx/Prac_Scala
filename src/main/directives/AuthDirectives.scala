package directives

import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import services.JwtService
import scala.util.{Success, Failure}

object AuthDirectives {
  def authenticated: Directive1[Int] = {
    authenticateOAuth2("Bearer", {
      case OAuth2BearerToken(token) =>
        JwtService.validateToken(token) match {
          case Success(claim) => 
            // Extract user_id from claim
            Some(claim.content.split(":")(1).split(",")(0).toInt)
          case Failure(_) => None
        }
    })
  }
}