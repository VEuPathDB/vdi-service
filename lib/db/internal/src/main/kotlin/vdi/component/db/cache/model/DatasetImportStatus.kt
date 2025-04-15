package vdi.component.db.cache.model

enum class DatasetImportStatus(val value: String) {
  Queued("queued"),
  InProgress("in-progress"),
  Complete("complete"),
  Invalid("invalid"),
  Failed("failed"),
  ;

  companion object {
    @JvmStatic
    fun fromString(value: String): DatasetImportStatus {
      for (enum in entries)
        if (enum.value == value)
          return enum

      throw IllegalArgumentException("unrecognized DatasetImportStatus value \"$value\"")
    }
  }
}