package vdi.model.data

fun UserID(value: Long): UserID {
  if (value < 1)
    throw IllegalArgumentException("User ID values cannot be less than 1")

  return LongUserID(value)
}

fun UserID(value: String): UserID {
  val long = try {
    value.toLong()
  } catch (e: Throwable) {
    throw IllegalArgumentException("User ID values must be integral numbers.")
  }

  return UserID(long)
}

@JvmInline
private value class LongUserID(val value: Long): UserID {
  override fun toLong() = value
  override fun toString() = value.toString()
}
