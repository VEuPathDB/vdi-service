package vdi.service.server.outputs

import vdi.service.generated.model.DatasetOwner
import vdi.service.generated.model.DatasetOwnerImpl
import vdi.service.model.UserDetails

fun DatasetOwner(user: UserDetails): DatasetOwner =
  DatasetOwnerImpl().apply {
    userId       = user.userID.toString().toLong()
    firstName    = user.firstName
    lastName     = user.lastName
    email        = user.email
    organization = user.organization
  }
