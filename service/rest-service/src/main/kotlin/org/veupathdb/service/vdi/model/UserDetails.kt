package org.veupathdb.service.vdi.model

import org.veupathdb.vdi.lib.common.field.UserID

data class UserDetails(
  val userID: UserID,
  val firstName: String?,
  val lastName: String?,
  val email: String?,
  val organization: String?,
)