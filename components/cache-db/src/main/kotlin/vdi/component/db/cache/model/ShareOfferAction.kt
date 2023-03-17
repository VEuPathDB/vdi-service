package vdi.component.db.cache.model

enum class ShareOfferAction(val value: String) {
  Grant("grant"),
  Revoke("revoke"),
  ;

  companion object {
    @JvmStatic
    fun fromString(value: String): ShareOfferAction {
      for (enum in values())
        if (enum.value == value)
          return enum

      throw IllegalArgumentException("unrecognized ShareOfferAction value \"$value\"")
    }
  }
}