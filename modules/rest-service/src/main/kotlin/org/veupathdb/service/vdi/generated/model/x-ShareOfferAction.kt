package org.veupathdb.service.vdi.generated.model

import vdi.component.db.cache.model.ShareOfferAction as SOA

fun ShareOfferAction(action: SOA): ShareOfferAction =
  when (action) {
    SOA.Grant  -> ShareOfferAction.GRANT
    SOA.Revoke -> ShareOfferAction.REVOKE
  }