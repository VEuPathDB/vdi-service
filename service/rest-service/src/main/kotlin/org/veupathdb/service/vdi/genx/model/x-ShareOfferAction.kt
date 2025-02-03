package org.veupathdb.service.vdi.genx.model

import org.veupathdb.service.vdi.generated.model.ShareOfferAction
import org.veupathdb.vdi.lib.common.model.VDIShareOfferAction

fun ShareOfferAction(action: VDIShareOfferAction): ShareOfferAction =
  when (action) {
    VDIShareOfferAction.Grant  -> ShareOfferAction.GRANT
    VDIShareOfferAction.Revoke -> ShareOfferAction.REVOKE
  }
