package vdi.component.db.cache.model

import org.veupathdb.vdi.lib.common.field.UserID
import java.time.OffsetDateTime

class BrokenImportListQuery {
  var userID: UserID? = null
  inline val hasUserID get() = userID != null

  var before: OffsetDateTime? = null
  inline val hasBefore get() = before != null

  var after: OffsetDateTime? = null
  inline val hasAfter get() = after != null

  var order: SortOrder = SortOrder.DESCENDING

  var sortBy: SortField = SortField.Date

  var limit: UInt = UInt.MAX_VALUE

  var offset: UInt = 0u

  enum class SortField {
    Date;

    override fun toString() = when (this) {
      Date -> "date"
    }

    companion object {
      @JvmStatic
      fun fromString(value: String) =
        fromStringOrNull(value)
          ?: throw IllegalArgumentException("unrecognized SortField value: $value")

      @JvmStatic
      fun fromStringOrNull(value: String) =
        when (value.lowercase()) {
          "date" -> Date
          else   -> null
        }
    }
  }
}
