package org.veupathdb.service.vdi.generated.model

import org.veupathdb.service.vdi.model.UserDetails

fun ShareOfferRecipient(user: UserDetails): ShareOfferRecipient =
  ShareOfferRecipientImpl().also {
    it.userID       = user.userID.toLong()
    it.firstName    = user.firstName
    it.lastName     = user.lastName
    it.email        = user.email
    it.organization = user.organization
  }