package vdi.components.common.fields

interface UserID {
  /**
   * Returns the string representation of this [UserID].
   */
  override fun toString(): String
}

fun UserID(userID: ULong): UserID = WDKUserID(userID)

fun UserID(userID: String): UserID = StringUserID(userID)

@JvmInline
internal value class WDKUserID(val value: ULong) : UserID {
  override fun toString() = value.toString()
}

@JvmInline
internal value class StringUserID(val value: String) : UserID {
  override fun toString() = value
}

fun Long.asWDKUserID(): UserID =
  if (this < 1)
    throw IllegalArgumentException("Invalid WDK user ID, cannot be less than 1")
  else
    WDKUserID(toULong())


fun String.toUserIDOrNull(): UserID? =
  if (isBlank()) null else StringUserID(this)