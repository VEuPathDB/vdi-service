package vdi.core.db.cache.model

enum class DatasetOwnershipFilter {
  ANY,
  OWNED,
  SHARED,
  ;

  override fun toString() = name.lowercase()

  companion object {

    @JvmStatic
    fun fromString(value: String) =
      fromStringOrNull(value)
        ?: throw IllegalArgumentException("unrecognized DatasetOwnershipFilter value $value")

    fun fromStringOrNull(value: String): DatasetOwnershipFilter? {
      val lowercase = value.lowercase()

      for (enumValue in values())
        if (enumValue.toString() == lowercase)
          return enumValue

      return null
    }
  }
}
