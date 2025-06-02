package vdi.lib.db.cache.model

import vdi.model.data.DatasetID
import vdi.model.data.UserID
import vdi.model.data.DatasetShareOffer

class ShareOfferRecord(
  val datasetID: DatasetID,
  val recipientID: UserID,
  action: Action
): DatasetShareOffer(action)

