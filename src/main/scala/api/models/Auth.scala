package api.models

case class LoginRequest(email: String, password: String)
case class LoginResponse(token: String)
case class RegisterRequest(name: String, email: String, password: String)