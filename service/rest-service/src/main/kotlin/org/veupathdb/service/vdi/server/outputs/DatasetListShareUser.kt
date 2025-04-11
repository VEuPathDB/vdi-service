package org.veupathdb.service.vdi.server.outputs

import org.veupathdb.service.vdi.generated.model.DatasetListShareUser
import org.veupathdb.service.vdi.generated.model.DatasetListShareUserImpl
import org.veupathdb.service.vdi.model.UserDetails

fun DatasetListShareUser(user: UserDetails, accepted: Boolean): DatasetListShareUser =
  DatasetListShareUserImpl().also {
    it.userId       = user.userID.toLong()
    it.firstName    = user.firstName
    it.lastName     = user.lastName
    it.organization = user.organization
    it.accepted     = accepted
  }
