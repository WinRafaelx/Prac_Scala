package domain

case class User(id: Option[Int], phone: Option[String], email: String, password: String)
case class LoginRequest(email: String, password: String)
case class LoginResponse(token: String)
case class RegisterRequest(phone: Option[String], email: String, password: String)