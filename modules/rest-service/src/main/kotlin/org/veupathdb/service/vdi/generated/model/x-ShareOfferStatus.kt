package org.veupathdb.service.vdi.generated.model

import org.veupathdb.service.vdi.model.ShareFilterStatus

internal fun ShareOfferStatus(status: ShareFilterStatus): ShareOfferStatus =
  when (status) {
    ShareFilterStatus.Open     -> ShareOfferStatus.OPEN
    ShareFilterStatus.Accepted -> ShareOfferStatus.ACCEPTED
    ShareFilterStatus.Rejected -> ShareOfferStatus.REJECTED
    ShareFilterStatus.All      -> throw IllegalArgumentException("cannot convert ShareStatus.All to IO type")
  }