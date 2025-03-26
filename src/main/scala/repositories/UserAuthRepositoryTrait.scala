package repositories

import domain.User
import scala.concurrent.{ExecutionContext, Future}

trait UserAuthRepositoryTrait {
  def createUser(user: User)(implicit ec: ExecutionContext): Future[User]
  def findByEmail(email: String)(implicit ec: ExecutionContext): Future[Option[User]]
}
