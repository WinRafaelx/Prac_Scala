package repository

import models.{User, UserTable}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api._
import db.DatabaseConnection.db

object UserRepository {
  private val users = TableQuery[UserTable]

  def getAllUsers(implicit ec: ExecutionContext): Future[List[User]] = {
    db.run(users.result).map(_.toList)
  }

  def getUser(id: Int)(implicit ec: ExecutionContext): Future[Option[User]] = {
    db.run(users.filter(_.id === id).result.headOption)
  }

  def createUser(user: User)(implicit ec: ExecutionContext): Future[User] = {
    db.run(users += user).map(_ => user)
  }

  def deleteUser(id: Int)(implicit ec: ExecutionContext): Future[Boolean] = {
    db.run(users.filter(_.id === id).delete).map(_ > 0)
  }

  def updateUser(id: Int, user: User)(implicit ec: ExecutionContext): Future[Option[User]] = {
    val updateQuery = users.filter(_.id === id).update(user)
    db.run(updateQuery).flatMap { rowsAffected =>
      if (rowsAffected > 0) Future.successful(Some(user))
      else Future.successful(None)
    }
  }

  def updateUserEmail(id: Int, email: String)(implicit ec: ExecutionContext): Future[Option[User]] = {
    val query = for {
      user <- users.filter(_.id === id)
    } yield user.email
    
    db.run(query.update(email)).flatMap { rowsAffected =>
      if (rowsAffected > 0) getUser(id)
      else Future.successful(None)
    }
  }
}
