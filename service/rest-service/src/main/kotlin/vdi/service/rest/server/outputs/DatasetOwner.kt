package vdi.service.rest.server.outputs

import vdi.service.rest.generated.model.DatasetOwner
import vdi.service.rest.generated.model.DatasetOwnerImpl
import vdi.service.model.UserDetails

fun DatasetOwner(user: UserDetails): vdi.service.rest.generated.model.DatasetOwner =
  vdi.service.rest.generated.model.DatasetOwnerImpl().apply {
    userId       = user.userID.toString().toLong()
    firstName    = user.firstName
    lastName     = user.lastName
    email        = user.email
    organization = user.organization
  }
