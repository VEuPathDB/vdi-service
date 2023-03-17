package vdi.component.db.cache.model

enum class ShareReceiptAction(val value: String) {
  Accept("accept"),
  Reject("reject"),
  ;

  companion object {
    @JvmStatic
    fun fromString(value: String): ShareReceiptAction {
      for (enum in values())
        if (enum.value == value)
          return enum

      throw IllegalArgumentException("unrecognized ShareReceiptAction value \"$value\"")
    }
  }
}