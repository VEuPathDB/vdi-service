package vdi.component.db.cache.model

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIShareOfferAction

data class DatasetShareOfferImpl(
  override val datasetID: DatasetID,
  override val recipientID: UserID,
  override val action: VDIShareOfferAction
) : DatasetShareOffer