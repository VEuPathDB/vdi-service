package org.veupathdb.service.vdi.genx.model

import org.veupathdb.service.vdi.generated.model.ShareOfferRecipient
import org.veupathdb.service.vdi.generated.model.ShareOfferRecipientImpl
import org.veupathdb.service.vdi.model.UserDetails

fun ShareOfferRecipient(user: UserDetails): ShareOfferRecipient =
  ShareOfferRecipientImpl().also {
    it.userId       = user.userID.toLong()
    it.firstName    = user.firstName
    it.lastName     = user.lastName
    it.email        = user.email
    it.organization = user.organization
  }
