package vdi.lib.db.cache.model

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetRevision
import org.veupathdb.vdi.lib.common.model.VDIDatasetRevisionAction
import java.time.OffsetDateTime

data class DatasetRevisionRecord(
  override val action: VDIDatasetRevisionAction,
  override val revisionID: DatasetID,
  override val revisionNote: String,
  override val timestamp: OffsetDateTime,
  val originalID: DatasetID,
) : VDIDatasetRevision
