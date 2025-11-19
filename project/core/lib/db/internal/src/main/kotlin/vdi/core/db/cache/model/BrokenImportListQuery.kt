package vdi.core.db.cache.model

import vdi.model.meta.UserID
import java.time.OffsetDateTime

data class BrokenImportListQuery(
  val userID: UserID?,
  val before: OffsetDateTime?,
  val after: OffsetDateTime?,
  val order: SortOrder,
  val sortBy: SortField,
  val limit: UInt,
  val offset: UInt,
) {
  inline val hasUserID get() = userID != null
  inline val hasBefore get() = before != null
  inline val hasAfter get() = after != null

  enum class SortField {
    Date;

    override fun toString() = when (this) {
      Date -> "date"
    }
  }
}
