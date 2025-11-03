package vdi.core.db.cache.model

enum class DatasetListSortField {
  CreationTimestamp,
  Name,
  ;

  override fun toString() = name.lowercase()

  companion object {
    @JvmStatic
    fun fromString(value: String) =
      fromStringOrNull(value)
        ?: throw IllegalArgumentException("unrecognized DatasetListSortField value $value")

    fun fromStringOrNull(value: String): DatasetListSortField? {
      val lowercase = value.lowercase()

      for (enumValue in entries)
        if (enumValue.toString() == lowercase)
          return enumValue

      return null
    }
  }
}
