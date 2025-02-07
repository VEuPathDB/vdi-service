package org.veupathdb.service.vdi.genx.model

import org.veupathdb.service.vdi.generated.model.DatasetOwner
import org.veupathdb.service.vdi.generated.model.DatasetOwnerImpl
import org.veupathdb.service.vdi.model.UserDetails

fun DatasetOwner(user: UserDetails): DatasetOwner =
  DatasetOwnerImpl().apply {
    userId       = user.userID.toString().toLong()
    firstName    = user.firstName
    lastName     = user.lastName
    email        = user.email
    organization = user.organization
  }
