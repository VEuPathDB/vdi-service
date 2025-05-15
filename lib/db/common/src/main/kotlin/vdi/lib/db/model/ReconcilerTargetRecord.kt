package vdi.lib.db.model

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import java.time.OffsetDateTime

open class ReconcilerTargetRecord(
  val ownerID: UserID,
  datasetID: DatasetID,
  sharesUpdated: OffsetDateTime,
  dataUpdated: OffsetDateTime,
  metaUpdated: OffsetDateTime,
  val type: VDIDatasetType,
  val isUninstalled: Boolean,
): SyncControlRecord(datasetID, sharesUpdated, dataUpdated, metaUpdated)
