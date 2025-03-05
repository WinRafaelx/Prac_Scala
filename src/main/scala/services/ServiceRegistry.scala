package services

import repository.{UserRepository, BookRepository}

/**
 * Service registry to centralize service instantiation and dependencies
 */
class ServiceRegistry {
  // Services
  lazy val jwtService: JwtService = JwtService()
  lazy val authService: AuthService = AuthService(UserRepository, jwtService)
  
  // Add more services as your application grows
}

object ServiceRegistry {
  def apply(): ServiceRegistry = new ServiceRegistry()
}