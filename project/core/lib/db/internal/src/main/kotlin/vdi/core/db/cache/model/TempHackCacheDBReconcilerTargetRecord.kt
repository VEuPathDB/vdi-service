package vdi.core.db.cache.model

import java.time.OffsetDateTime
import vdi.core.db.model.ReconcilerTargetRecord
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetType
import vdi.model.meta.UserID

// FIXME: Remove this class when the target db delete logic is moved from the
//        reconciler to the hard-delete lane.
class TempHackCacheDBReconcilerTargetRecord(
  ownerID: UserID,
  datasetID: DatasetID,
  sharesUpdated: OffsetDateTime,
  dataUpdated: OffsetDateTime,
  metaUpdated: OffsetDateTime,
  type: DatasetType,
  isUninstalled: Boolean,

  val inserted: OffsetDateTime,
  val importStatus: DatasetImportStatus,
): ReconcilerTargetRecord(ownerID, datasetID, sharesUpdated, dataUpdated, metaUpdated, type, isUninstalled)