package repository

import models.User
import db.tables.UserTable
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api._
import db.DatabaseConnection.db
import org.slf4j.LoggerFactory

object UserRepository extends UserRepositoryTrait {
  private val users = UserTable.table
  private val logger = LoggerFactory.getLogger(this.getClass)

  /** Fetch all users */
  def getAllUsers(implicit ec: ExecutionContext): Future[List[User]] = {
    db.run(users.result)
      .map(_.toList)
      .recoverWith { case e =>
        logger.error("Failed to get all users", e)
        Future.failed(e)
      }
  }

  /** Fetch a user by ID */
  def getUser(id: Int)(implicit ec: ExecutionContext): Future[Option[User]] = {
    db.run(users.filter(_.id === id).result.headOption)
      .recoverWith { case e =>
        logger.error(s"Failed to get user $id", e)
        Future.failed(e)
      }
  }

  /** Create a new user (returns created user with ID) */
  def createUser(user: User)(implicit ec: ExecutionContext): Future[User] = {
    val insertQuery = (users returning users.map(_.id) into ((user, id) => user.copy(id = Some(id))))
    db.run(insertQuery += user.copy(id = None)) // Ensure ID is not manually set
      .recoverWith { case e =>
        logger.error("Failed to create user", e)
        Future.failed(e)
      }
  }

  /** Delete user by ID (returns true if deleted) */
  def deleteUser(id: Int)(implicit ec: ExecutionContext): Future[Boolean] = {
    db.run(users.filter(_.id === id).delete)
      .map(_ > 0)
      .recoverWith { case e =>
        logger.error(s"Failed to delete user $id", e)
        Future.failed(e)
      }
  }

  /** Find user by email */
  def findByEmail(email: String)(implicit ec: ExecutionContext): Future[Option[User]] = {
    db.run(users.filter(_.email === email).result.headOption)
      .recoverWith { case e =>
        logger.error(s"Failed to find user by email $email", e)
        Future.failed(e)
      }
  }

  /** Update user details */
  def updateUser(id: Int, user: User)(implicit ec: ExecutionContext): Future[Option[User]] = {
    val updateAction = users.filter(_.id === id).map(u => (u.name, u.email, u.password))
      .update((user.name, user.email, user.password))

    db.run(updateAction.transactionally).flatMap {
      case 0 => Future.successful(None) // No user found
      case _ => getUser(id) // Return updated user
    }.recoverWith { case e =>
      logger.error(s"Failed to update user $id", e)
      Future.failed(e)
    }
  }

  /** Update user's email */
  def updateUserEmail(id: Int, email: String)(implicit ec: ExecutionContext): Future[Option[User]] = {
    val query = users.filter(_.id === id).map(_.email).update(email)
    
    db.run(query.transactionally).flatMap {
      case 0 => Future.successful(None) // User not found
      case _ => getUser(id) // Return updated user
    }.recoverWith { case e =>
      logger.error(s"Failed to update email for user $id", e)
      Future.failed(e)
    }
  }

  /** Count total users */
  def count(implicit ec: ExecutionContext): Future[Int] = {
    db.run(users.length.result)
      .recoverWith { case e =>
        logger.error("Failed to count users", e)
        Future.failed(e)
      }
  }
}
