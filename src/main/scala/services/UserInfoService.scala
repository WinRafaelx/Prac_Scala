package services

import domain.UserInfo
import repositories.UserInfoRepository

import java.util.UUID
import java.sql.Timestamp
import scala.concurrent.{ExecutionContext, Future}

class UserInfoService(userInfoRepository: UserInfoRepository)(implicit ec: ExecutionContext) {

  def createUserInfo(
      userAuthId: UUID,
      fullName: String,
      dateOfBirth: Option[java.sql.Date],
      passportNumber: Option[String],
      nationality: Option[String],
      address: Option[String]
  ): Future[UserInfo] = {
    val userInfo = UserInfo(
      id = UUID.randomUUID(),
      userAuthId = userAuthId,
      fullName = fullName,
      dateOfBirth = dateOfBirth,
      passportNumber = passportNumber,
      nationality = nationality,
      address = address,
      createdAt = new Timestamp(System.currentTimeMillis())
    )
    userInfoRepository.create(userInfo).map(_ => userInfo)
  }

  def getUserInfoById(id: UUID): Future[Option[UserInfo]] = {
    userInfoRepository.findById(id)
  }

  def getUserInfoByUserAuthId(userAuthId: UUID): Future[Option[UserInfo]] = {
    userInfoRepository.findByUserAuthId(userAuthId)
  }

  def updateUserInfo(userInfo: UserInfo): Future[Int] = {
    userInfoRepository.update(userInfo)
  }

  def deleteUserInfo(id: UUID): Future[Int] = {
    userInfoRepository.delete(id)
  }

  def listAllUserInfo(): Future[Seq[UserInfo]] = {
    userInfoRepository.listAll()
  }
}

object UserInfoService {
  def apply(userInfoRepository: UserInfoRepository)(implicit ec: ExecutionContext): UserInfoService =
    new UserInfoService(userInfoRepository)
}
