@file:JvmName("DatasetServiceUtils")
package vdi.service.rest.server.services.dataset

import vdi.model.data.UserID
import vdi.service.rest.model.UserDetails

internal fun Map<UserID, UserDetails>.requireDetails(userID: UserID) =
  get(userID) ?: throw IllegalStateException("missing user details for $userID")

