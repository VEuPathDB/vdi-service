package vdi.lib.db.cache.model

import vdi.model.data.DatasetID
import vdi.model.data.DatasetRevision

class DatasetRevisionRecordSet(
  val originalID: DatasetID,
  val records: List<DatasetRevision>,
)
