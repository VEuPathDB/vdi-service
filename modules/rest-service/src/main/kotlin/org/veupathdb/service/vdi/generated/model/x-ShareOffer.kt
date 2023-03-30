package org.veupathdb.service.vdi.generated.model

import org.veupathdb.service.vdi.model.UserDetails
import org.veupathdb.vdi.lib.common.model.VDIShareOfferAction

fun ShareOffer(user: UserDetails, action: VDIShareOfferAction): ShareOffer =
  ShareOfferImpl().also {
    it.recipient = ShareOfferRecipient(user)
    it.status    = ShareOfferAction(action)
  }