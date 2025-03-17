package repositories

import domain.{UserInfo, UserInfoTable}
import slick.jdbc.PostgresProfile.api._

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import java.sql.Timestamp

class UserInfoRepository(db: Database)(implicit ec: ExecutionContext) {

  def create(userInfo: UserInfo): Future[Int] = {
    db.run(UserInfoTable.table += userInfo)
  }

  def findById(id: UUID): Future[Option[UserInfo]] = {
    db.run(UserInfoTable.table.filter(_.id === id).result.headOption)
  }

  def findByUserAuthId(userAuthId: UUID): Future[Option[UserInfo]] = {
    db.run(UserInfoTable.table.filter(_.userAuthId === userAuthId).result.headOption)
  }

  def update(userInfo: UserInfo): Future[Int] = {
    db.run(UserInfoTable.table.filter(_.id === userInfo.id).update(userInfo))
  }

  def delete(id: UUID): Future[Int] = {
    db.run(UserInfoTable.table.filter(_.id === id).delete)
  }

  def listAll(): Future[Seq[UserInfo]] = {
    db.run(UserInfoTable.table.result)
  }
}
