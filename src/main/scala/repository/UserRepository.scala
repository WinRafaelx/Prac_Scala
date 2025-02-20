package repository

import models.User
import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object UserRepository {
  private val users: mutable.Map[Int, User] = mutable.Map()

  def getAllUsers: Future[List[User]] = Future {
    users.values.toList
  }

  def getUser(id: Int): Future[Option[User]] = Future {
    users.get(id)
  }

  def createUser(user: User): Future[User] = Future {
    users += (user.id -> user)
    user
  }

  def deleteUser(id: Int): Future[Boolean] = Future {
    users.remove(id).isDefined
  }

  def updateUser(id: Int, user: User): Future[Option[User]] = Future {
    users.get(id) match {
      case Some(_) =>
        users.update(id, user)
        Some(user)
      case None => None
    }
  }

  def updateUserEmail(id: Int, email: String): Future[Option[User]] = Future {
    users.get(id) match {
      case Some(user) =>
        val updatedUser = user.copy(email = email)
        users.update(id, updatedUser)
        Some(updatedUser)
      case None => None
    }
  }
}
