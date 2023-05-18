package org.veupathdb.vdi.lib.db.cache.model

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIShareReceiptAction

data class DatasetShareListEntry(
  val datasetID: DatasetID,
  val ownerID: UserID,
  val typeName: String,
  val typeVersion: String,
  val receiptStatus: VDIShareReceiptAction?,
  val projects: List<ProjectID>
)