package vdi.lib.db.cache.model

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIShareOfferAction

interface DatasetShareOffer {
  val datasetID: DatasetID
  val recipientID: UserID
  val action: VDIShareOfferAction
}

