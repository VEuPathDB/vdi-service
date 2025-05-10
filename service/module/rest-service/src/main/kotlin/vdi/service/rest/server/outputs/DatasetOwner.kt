package vdi.service.rest.server.outputs

import vdi.service.rest.generated.model.DatasetOwner
import vdi.service.rest.model.UserDetails

fun DatasetOwner(user: UserDetails): DatasetOwner =
  vdi.service.rest.generated.model.DatasetOwnerImpl().apply {
    userId       = user.userID.toString().toLong()
    firstName    = user.firstName
    lastName     = user.lastName
    email        = user.email
    organization = user.organization
  }
