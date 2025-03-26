package repositories

import domain.{UserAuth, UserAuthTable}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api._
import db.DatabaseConnection.db
import org.slf4j.LoggerFactory
import java.sql.Timestamp
import java.time.Instant
import api.models.User

object UserAuthRepository extends UserAuthRepositoryTrait {
  private val users = UserAuthTable.table
  private val logger = LoggerFactory.getLogger(this.getClass)

  // Conversion between domain model and API model
  private def toApiUser(userAuth: UserAuth): User = {
    if (userAuth == null) {
      logger.error("Attempted to convert null UserAuth to User")
      throw new IllegalArgumentException("UserAuth cannot be null")
    }

    try {
      User(
        id = Some(userAuth.id),
        phone = userAuth.phone,
        email = userAuth.email,
        password =
          userAuth.passwordHash // We need to return the hash for password verification
      )
    } catch {
      case e: Exception =>
        logger.error(s"Error converting UserAuth to User: ${e.getMessage}")
        throw e
    }
  }

  private def toUserAuth(user: User): UserAuth = {
    UserAuth(
      id = user.id.getOrElse(
        0
      ), // Default to 0 for new users, DB will assign actual ID
      email = user.email,
      phone = user.phone,
      passwordHash =
        user.password, // Note: In a real app, this should be hashed
      authProvider = "email",
      role = "user",
      createdAt = new Timestamp(System.currentTimeMillis())
    )
  }

  /** Fetch all users */
  def getAllUsers(implicit ec: ExecutionContext): Future[List[User]] = {
    db.run(users.result)
      .map(_.map(toApiUser).toList)
      .recoverWith { case e =>
        logger.error("Failed to get all users", e)
        Future.failed(e)
      }
  }

  /** Fetch a user by ID */
  def getUser(id: Int)(implicit ec: ExecutionContext): Future[Option[User]] = {
    // Direct query by Int ID
    db.run(users.filter(_.id === id).result.headOption)
      .map(_.map(toApiUser))
      .recoverWith { case e =>
        logger.error(s"Failed to get user $id", e)
        Future.failed(e)
      }
  }

  /** Create a new user (returns created user with ID) */
  def createUser(user: User)(implicit ec: ExecutionContext): Future[User] = {
    val userAuth = toUserAuth(user)
    // Use returning to get the auto-generated ID
    db.run((users returning users.map(_.id)) += userAuth)
      .map(id => toApiUser(userAuth.copy(id = id)))
      .recoverWith { case e =>
        logger.error("Failed to create user", e)
        Future.failed(e)
      }
  }

  /** Delete user by ID (returns true if deleted) */
  def deleteUser(id: Int)(implicit ec: ExecutionContext): Future[Boolean] = {
    // Direct delete by Int ID
    db.run(users.filter(_.id === id).delete)
      .map(_ > 0)
      .recoverWith { case e =>
        logger.error(s"Failed to delete user $id", e)
        Future.failed(e)
      }
  }

  /** Find user by email */
  def findByEmail(
      email: String
  )(implicit ec: ExecutionContext): Future[Option[User]] = {
    // Debug the query first
    logger.debug(s"Finding user by email: $email")

    db.run(users.filter(_.email === email).result.headOption)
      .map { maybeUserAuth =>
        // Add debug to check if result is found
        logger.debug(s"Found user auth: $maybeUserAuth")
        maybeUserAuth.map(toApiUser)
      }
      .recoverWith { case e =>
        logger.error(s"Failed to find user by email $email", e)
        Future.failed(e)
      }
  }

  def updateUser(
      id: Int,
      email: Option[String] = None,
      passwordHash: Option[String] = None,
      phone: Option[String] = None,
      role: Option[String] = None
  )(implicit ec: ExecutionContext): Future[Option[User]] = {
    // Get user by ID directly
    getUser(id).flatMap {
      case Some(user) =>
        val userAuth = toUserAuth(user)
        val query = users
          .filter(_.id === id) // Direct filter by Int ID
          .map(u => (u.email, u.passwordHash, u.phone, u.role))
          .update(
            (
              email.getOrElse(userAuth.email),
              passwordHash.getOrElse(userAuth.passwordHash),
              phone.orElse(userAuth.phone), // Handle nullable field
              role.getOrElse(userAuth.role)
            )
          )

        db.run(query)
          .flatMap {
            case 0 => Future.successful(None) // No rows affected
            case _ => findByEmail(email.getOrElse(userAuth.email))
          }
          .recoverWith { case e =>
            logger.error(s"Failed to update user $id", e)
            Future.failed(e)
          }

      case None =>
        Future.successful(None)
    }
  }

}
