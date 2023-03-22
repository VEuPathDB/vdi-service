package org.veupathdb.service.vdi.model

import vdi.components.common.fields.UserID

data class UserDetails(
  val userID: UserID,
  val firstName: String?,
  val lastName: String?,
  val email: String?,
  val organization: String?,
)