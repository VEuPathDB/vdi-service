package org.veupathdb.service.vdi.generated.model

import org.veupathdb.service.vdi.model.UserDetails
import vdi.component.db.cache.model.ShareOfferAction

fun ShareOffer(user: UserDetails, action: ShareOfferAction): ShareOffer =
  ShareOfferImpl().also {
    it.recipient = ShareOfferRecipient(user)
    it.status    = ShareOfferAction(action)
  }