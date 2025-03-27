package repositories

import api.models.{UserAuth, UserAuthTable}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api._
import db.DatabaseConnection.db
import org.slf4j.LoggerFactory
import java.sql.Timestamp
import java.time.Instant
import domain.User

object UserAuthRepository extends UserAuthRepositoryTrait {
  private val users = UserAuthTable.query
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
}
