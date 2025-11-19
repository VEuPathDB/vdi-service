package vdi.core.db.cache.model

import vdi.model.meta.DatasetID
import vdi.model.meta.UserID
import vdi.model.meta.DatasetShareOffer

class ShareOfferRecord(
  val datasetID: DatasetID,
  val recipientID: UserID,
  action: Action
): DatasetShareOffer(action)

