package vdi.core.db.model

import vdi.model.meta.DatasetID
import vdi.model.meta.UserID
import vdi.model.meta.DatasetType
import java.time.OffsetDateTime

open class ReconcilerTargetRecord(
  val ownerID: UserID,
  datasetID: DatasetID,
  sharesUpdated: OffsetDateTime,
  dataUpdated: OffsetDateTime,
  metaUpdated: OffsetDateTime,
  val type: DatasetType,
  val isUninstalled: Boolean,
): SyncControlRecord(datasetID, sharesUpdated, dataUpdated, metaUpdated) {
  override fun toString() = "$ownerID/$datasetID"
}