package vdi.core.db.cache.model

import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetRevision

class DatasetRevisionRecordSet(
  val originalID: DatasetID,
  val records: List<DatasetRevision>,
)
