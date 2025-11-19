package vdi.service.rest.model

import vdi.model.meta.UserID

data class UserDetails(
  val userID: UserID,
  val firstName: String?,
  val lastName: String?,
  val email: String?,
  val organization: String?,
)
