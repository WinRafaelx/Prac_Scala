package api.models

case class User(id: Option[Int], name: String, email: String, password: String)
