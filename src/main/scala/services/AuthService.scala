package services

import api.models.{LoginRequest, LoginResponse, RegisterRequest, User}
import repositories.UserRepositoryTrait
import com.github.t3hnar.bcrypt._
import scala.concurrent.{ExecutionContext, Future}

class AuthService(userRepository: UserRepositoryTrait, jwtService: JwtService) {
  
  def register(request: RegisterRequest)(implicit ec: ExecutionContext): Future[Either[String, (String, User)]] = {
    val hashedPassword = request.password.bcrypt
    val newUser = User(None, request.name, request.email, hashedPassword)
    
    userRepository.createUser(newUser).map { createdUser =>
      val token = jwtService.createToken(createdUser.id, createdUser.email)
      Right((token, createdUser))
    }.recover {
      case e: Exception => Left(s"Registration failed: ${e.getMessage}")
    }
  }
  
  def login(request: LoginRequest)(implicit ec: ExecutionContext): Future[Either[String, String]] = {
    userRepository.findByEmail(request.email).map {
      case Some(user) if request.password.isBcrypted(user.password) =>
        val token = jwtService.createToken(user.id, user.email)
        Right(token)
      case Some(_) => 
        Left("Invalid credentials")
      case None => 
        Left("User not found")
    }.recover {
      case e: Exception => Left(s"Login failed: ${e.getMessage}")
    }
  }
  
  def validateToken(token: String)(implicit ec: ExecutionContext): Future[Option[Int]] = {
    Future.successful {
      jwtService.validateToken(token).toOption.flatMap { claim =>
        try {
          val content = claim.content
          // Extract user_id from content
          val userIdPattern = """"user_id":(\d+)""".r
          userIdPattern.findFirstMatchIn(content).map(_.group(1).toInt)
        } catch {
          case _: Exception => None
        }
      }
    }
  }
}

object AuthService {
  def apply(userRepository: UserRepositoryTrait, jwtService: JwtService): AuthService = 
    new AuthService(userRepository, jwtService)
}