package vdi.component.db.cache.model

import java.time.OffsetDateTime
import vdi.components.common.fields.DatasetID
import vdi.components.common.fields.UserID

interface Dataset {
  val datasetID: DatasetID
  val typeName: String
  val typeVersion: String
  val ownerID: UserID
  val isDeleted: Boolean
  val created: OffsetDateTime
  val importStatus: DatasetImportStatus
}

enum class DatasetImportStatus(val value: String) {
  AwaitingImport("awaiting-import"),
  Importing("importing"),
  Imported("imported"),
  ImportFailed("import-failed"),
  ;

  companion object {
    @JvmStatic
    fun fromString(value: String): DatasetImportStatus {
      for (enum in values())
        if (enum.value == value)
          return enum

      throw IllegalArgumentException("unrecognized DatasetImportStatus value \"$value\"")
    }
  }
}