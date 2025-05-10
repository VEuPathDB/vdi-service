package vdi.lib.db.cache.model

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetRevision

class DatasetRevisionRecordSet(
  val originalID: DatasetID,
  val records: List<VDIDatasetRevision>,
)
