package org.veupathdb.vdi.lib.db.cache.model

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