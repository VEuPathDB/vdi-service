package org.veupathdb.service.vdi.db.internal.model

import vdi.components.common.fields.DatasetID

interface DatasetMeta {
  val datasetID: DatasetID
  val name: String
  val summary: String?
  val description: String?
}