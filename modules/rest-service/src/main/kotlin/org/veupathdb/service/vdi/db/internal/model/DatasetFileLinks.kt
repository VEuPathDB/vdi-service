package org.veupathdb.service.vdi.db.internal.model

import vdi.components.common.fields.DatasetID

interface DatasetFileLinks {
  val datasetID: DatasetID
  val files: Collection<String>
}