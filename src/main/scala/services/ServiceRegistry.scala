package services

import repository.{UserRepository, BookRepository}
import config.Environment

/**
 * Service registry to centralize service instantiation and dependencies
 */
class ServiceRegistry {
  // Services
  lazy val jwtService: JwtService = try {
    JwtService()
  } catch {
    case e: IllegalArgumentException =>
      println(s"❌ Error initializing JWT service: ${e.getMessage}")
      println("Please create a .env file with JWT_SECRET_KEY or set the environment variable")
      throw e
  }
  
  lazy val authService: AuthService = AuthService(UserRepository, jwtService)
  
  // Add more services as your application grows
}

object ServiceRegistry {
  def apply(): ServiceRegistry = new ServiceRegistry()
}