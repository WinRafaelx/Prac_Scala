package repository

import models.User
import db.tables.UserTable
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api._
import db.DatabaseConnection.db

object UserRepository {
  private val users = UserTable.table

  def getAllUsers(implicit ec: ExecutionContext): Future[List[User]] = {
    db.run(users.result).map(_.toList)
  }

  def getUser(id: Int)(implicit ec: ExecutionContext): Future[Option[User]] = {
    db.run(users.filter(_.id === id).result.headOption)
  }

  def createUser(user: User)(implicit ec: ExecutionContext): Future[User] = {
    val insertQuery = users returning users.map(_.id) into ((user, id) => user.copy(id = id))
    db.run(insertQuery += user)
  }

  def deleteUser(id: Int)(implicit ec: ExecutionContext): Future[Boolean] = {
    db.run(users.filter(_.id === id).delete).map(_ > 0)
  }

  // Add this method to your existing UserRepository object
  def findByEmail(email: String)(implicit ec: ExecutionContext): Future[Option[User]] = {
    db.run(users.filter(_.email === email).result.headOption)
  }

  def updateUser(id: Int, user: User)(implicit ec: ExecutionContext): Future[Option[User]] = {
    val updateUser = user.copy(id = id)
    val updateQuery = users.filter(_.id === id).update(updateUser)
    db.run(updateQuery).flatMap { 
      case 0 => Future.successful(None)
      case _ => Future.successful(Some(updateUser))
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
