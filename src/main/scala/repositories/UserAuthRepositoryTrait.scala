package repositories

import api.models.User
import scala.concurrent.{ExecutionContext, Future}

trait UserAuthRepositoryTrait {
  def getAllUsers(implicit ec: ExecutionContext): Future[List[User]]
  def getUser(id: Int)(implicit ec: ExecutionContext): Future[Option[User]]
  def createUser(user: User)(implicit ec: ExecutionContext): Future[User]
  def deleteUser(id: Int)(implicit ec: ExecutionContext): Future[Boolean]
  def findByEmail(email: String)(implicit
      ec: ExecutionContext
  ): Future[Option[User]]
  def updateUser(
      id: Int,
      email: Option[String] = None,
      passwordHash: Option[String] = None,
      phone: Option[String] = None,
      role: Option[String] = None
  )(implicit ec: ExecutionContext): Future[Option[User]]
}
