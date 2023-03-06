package org.veupathdb.service.vdi.db.internal.model

import vdi.components.common.fields.DatasetID

interface DatasetProjectLinks {
  val datasetID: DatasetID
  val projects: Collection<String>
}