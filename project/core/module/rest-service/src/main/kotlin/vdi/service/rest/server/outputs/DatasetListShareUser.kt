package vdi.service.rest.server.outputs

import vdi.service.rest.generated.model.DatasetListShareUser
import vdi.service.rest.generated.model.DatasetListShareUserImpl
import vdi.service.rest.model.UserDetails

fun DatasetListShareUser(user: UserDetails, accepted: Boolean): DatasetListShareUser =
  DatasetListShareUserImpl().also {
    it.userId       = user.userID.toLong()
    it.firstName    = user.firstName
    it.lastName     = user.lastName
    it.organization = user.organization
    it.accepted     = accepted
  }
