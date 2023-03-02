package vdi.components.common.fields

@JvmInline
value class WDKUserID(val value: ULong) : UserID {
  override fun toString() = value.toString()
}

fun Long.asWDKUserID(): WDKUserID =
  if (this < 1)
    throw IllegalArgumentException("Invalid WDK user ID, cannot be less than 1")
  else
    WDKUserID(toULong())
