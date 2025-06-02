package vdi.lib.db.model

import vdi.model.data.DatasetID
import vdi.model.data.UserID
import vdi.model.data.DatasetType
import java.time.OffsetDateTime

open class ReconcilerTargetRecord(
  val ownerID: UserID,
  datasetID: DatasetID,
  sharesUpdated: OffsetDateTime,
  dataUpdated: OffsetDateTime,
  metaUpdated: OffsetDateTime,
  val type: DatasetType,
  val isUninstalled: Boolean,
): SyncControlRecord(datasetID, sharesUpdated, dataUpdated, metaUpdated)
