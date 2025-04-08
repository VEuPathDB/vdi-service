package org.veupathdb.service.vdi.service.dataset

import org.veupathdb.service.vdi.model.UserDetails
import org.veupathdb.vdi.lib.common.field.UserID

internal fun Map<UserID, UserDetails>.requireDetails(userID: UserID) =
  get(userID) ?: throw IllegalStateException("missing user details for $userID")
