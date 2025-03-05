package services

import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}
import java.time.Clock
import scala.util.Try
import config.Environment

class JwtService(secretKey: String, expirationSeconds: Int = 3600) {
  private val algorithm = JwtAlgorithm.HS256
  implicit val clock: Clock = Clock.systemUTC()

  def createToken(userId: Option[Int], email: String): String = {
    val id = userId.getOrElse(0)
    val claim = JwtClaim(
      content = s"""{"user_id":$id,"email":"$email"}""",
      expiration = Some(clock.instant().plusSeconds(expirationSeconds).getEpochSecond), 
      issuedAt = Some(clock.instant().getEpochSecond)
    )
    JwtCirce.encode(claim, secretKey, algorithm)
  }

  def validateToken(token: String): Try[JwtClaim] = {
    JwtCirce.decode(token, secretKey, Seq(algorithm))
  }
}

object JwtService {
  // Get JWT secret from environment or .env file
  private val secretKey = Environment.getRequired("JWT_SECRET_KEY")
  private val expiration = Environment.getInt("JWT_EXPIRATION", 3600)
  
  def apply(): JwtService = new JwtService(secretKey, expiration)
  def apply(secretKey: String): JwtService = new JwtService(secretKey)
  def apply(secretKey: String, expiration: Int): JwtService = new JwtService(secretKey, expiration)
}