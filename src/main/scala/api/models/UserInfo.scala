package api.models

import java.util.UUID
import java.sql.Timestamp

case class UserInfoRequest(
  userAuthId: UUID,
  fullName: String,
  dateOfBirth: Option[java.sql.Date],
  passportNumber: Option[String],
  nationality: Option[String],
  address: Option[String]
)

case class UserInfoResponse(
  id: UUID,
  userAuthId: UUID,
  fullName: String,
  dateOfBirth: Option[java.sql.Date],
  passportNumber: Option[String],
  nationality: Option[String],
  address: Option[String],
  createdAt: Timestamp
)

object UserInfoResponse {
  def fromDomain(userInfo: domain.UserInfo): UserInfoResponse =
    UserInfoResponse(
      id = userInfo.id,
      userAuthId = userInfo.userAuthId,
      fullName = userInfo.fullName,
      dateOfBirth = userInfo.dateOfBirth,
      passportNumber = userInfo.passportNumber,
      nationality = userInfo.nationality,
      address = userInfo.address,
      createdAt = userInfo.createdAt
    )
}
