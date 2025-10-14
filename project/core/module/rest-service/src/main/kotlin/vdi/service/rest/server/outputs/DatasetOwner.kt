package vdi.service.rest.server.outputs

import vdi.service.rest.generated.model.DatasetOwner
import vdi.service.rest.generated.model.DatasetOwnerImpl
import vdi.service.rest.model.UserDetails

fun DatasetOwner(user: UserDetails): DatasetOwner =
  DatasetOwnerImpl().apply {
    userId       = user.userID.toString().toLong()
    firstName    = user.firstName
    lastName     = user.lastName
    email        = user.email
    affiliation  = user.organization
  }
