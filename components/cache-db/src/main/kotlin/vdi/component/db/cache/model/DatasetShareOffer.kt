package vdi.component.db.cache.model

import org.veupathdb.service.vdi.generated.model.ShareOfferAction
import vdi.components.common.fields.DatasetID
import vdi.components.common.fields.UserID

interface DatasetShareOffer {
  val datasetID: DatasetID
  val recipientID: UserID
  val action: ShareOfferAction
}

