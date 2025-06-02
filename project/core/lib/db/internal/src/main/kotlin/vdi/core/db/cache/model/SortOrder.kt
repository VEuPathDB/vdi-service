package vdi.core.db.cache.model

enum class SortOrder {
  Ascending,
  Descending,
  ;

  override fun toString() = when (this) {
    Ascending  -> "ASC"
    Descending -> "DESC"
  }

  companion object {
    fun fromString(value: String) =
      fromStringOrNull(value)
        ?: throw IllegalArgumentException("unrecognized SortOrder value $value")

    fun fromStringOrNull(value: String): SortOrder? {
      val uppercase = value.uppercase()

      for (enumValue in entries)
        if (enumValue.toString() == uppercase)
          return enumValue

      return null
    }
  }
}
