package services

import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}
import java.time.Clock
import scala.util.{Success, Failure, Try}

object JwtService {
  private val secretKey = "your-256-bit-secret"  // In production, use environment variable
  private val algorithm = JwtAlgorithm.HS256
  implicit val clock: Clock = Clock.systemUTC()

  def createToken(userId: Int, email: String): String = {
    val claim = JwtClaim(
      content = s"""{"user_id":$userId,"email":"$email"}""",
      expiration = Some(clock.instant().plusSeconds(3600).getEpochSecond), // 1 hour
      issuedAt = Some(clock.instant().getEpochSecond)
    )
    JwtCirce.encode(claim, secretKey, algorithm)
  }

  def validateToken(token: String): Try[JwtClaim] = {
    JwtCirce.decode(token, secretKey, Seq(algorithm))
  }
}