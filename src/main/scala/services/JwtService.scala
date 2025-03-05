package services

import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}
import java.time.Clock
import scala.util.Try

class JwtService(secretKey: String) {
  private val algorithm = JwtAlgorithm.HS256
  implicit val clock: Clock = Clock.systemUTC()

  def createToken(userId: Option[Int], email: String): String = {
    val id = userId.getOrElse(0)
    val claim = JwtClaim(
      content = s"""{"user_id":$id,"email":"$email"}""",
      expiration = Some(clock.instant().plusSeconds(3600).getEpochSecond), // 1 hour
      issuedAt = Some(clock.instant().getEpochSecond)
    )
    JwtCirce.encode(claim, secretKey, algorithm)
  }

  def validateToken(token: String): Try[JwtClaim] = {
    JwtCirce.decode(token, secretKey, Seq(algorithm))
  }
}

object JwtService {
  // For production, fetch from environment variable or config
  private val defaultSecretKey = "your-256-bit-secret" 
  
  def apply(): JwtService = new JwtService(defaultSecretKey)
  def apply(secretKey: String): JwtService = new JwtService(secretKey)
}