package repository

import models.User
import scala.concurrent.{ExecutionContext, Future}

trait UserRepositoryTrait {
  def getAllUsers(implicit ec: ExecutionContext): Future[List[User]]
  def getUser(id: Int)(implicit ec: ExecutionContext): Future[Option[User]]
  def createUser(user: User)(implicit ec: ExecutionContext): Future[User]
  def deleteUser(id: Int)(implicit ec: ExecutionContext): Future[Boolean]
  def findByEmail(email: String)(implicit ec: ExecutionContext): Future[Option[User]]
  def updateUser(id: Int, user: User)(implicit ec: ExecutionContext): Future[Option[User]]
  def updateUserEmail(id: Int, email: String)(implicit ec: ExecutionContext): Future[Option[User]]
  def count(implicit ec: ExecutionContext): Future[Int]
}