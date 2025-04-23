package vdi.lib.db.cache.model

import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import java.time.OffsetDateTime

interface Dataset {
  val datasetID: DatasetID
  val typeName: DataType
  val typeVersion: String
  val ownerID: UserID
  val isDeleted: Boolean
  val created: OffsetDateTime
  val importStatus: DatasetImportStatus
  val origin: String
  val inserted: OffsetDateTime
}
