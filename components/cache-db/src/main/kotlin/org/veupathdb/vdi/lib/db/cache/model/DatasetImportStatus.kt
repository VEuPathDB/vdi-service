package org.veupathdb.vdi.lib.db.cache.model

enum class DatasetImportStatus(val value: String) {
  AwaitingImport("queued"),
  Importing("in-progress"),
  Imported("complete"),
  ImportFailed("invalid"),
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