package vdi.components.common.fields

import java.util.UUID

/**
 * Dataset Identifier
 *
 * An opaque identifier that is unique to a single VDI dataset.
 *
 * @since 1.0.0
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 */
sealed interface DatasetID

@JvmInline
private value class StringDatasetID(val value: String) : DatasetID

/**
 * Attempts to construct a new [DatasetID] instance from the given string value.
 *
 * If the given string value could not be a valid [DatasetID] value, an
 * [IllegalArgumentException] will be thrown.
 *
 * For a string value to appear valid, it must be a 32 digit string consisting
 * of only hex characters.
 *
 * For example:
 * * `78609580dba2787a5cf677d6f334a707`
 * * `A5FA4B2B38398ADD73D5929F3DCFDEA8`
 *
 * @param raw Raw string value that should be parsed as a [DatasetID] instance.
 *
 * @return A [DatasetID] instance parsed from the given [raw] string.
 *
 * @throws IllegalArgumentException If the given raw string is not 32 characters
 * in length, or contains a non-hex character.
 */
fun DatasetID(raw: String): DatasetID {
  if (raw.length != 32)
    throw IllegalArgumentException("invalid Dataset ID string, must be exactly 32 hex characters")

  for (c in raw)
    if (!c.isHex())
      throw IllegalArgumentException("invalid Dataset ID string, must contain only hex characters")

  return StringDatasetID(raw.uppercase())
}

/**
 * Generates a new dataset, random dataset ID.
 *
 * Generated IDs are not guaranteed to be 100% unique, however the chance of
 * collision with another dataset ID is 1 in 2.71 quintillion, and thus may be
 * safely considered unique for most applications.
 *
 * @return A new random dataset ID.
 */
fun DatasetID(): DatasetID {
  val start  = UUID.randomUUID().toString()
  val buffer = CharArray(32)

  var i = 0
  for (c in start)
    if (c.isHex())
      buffer[i++] = c

  if (i != 32)
    throw IllegalStateException("what the literal heck (a uuid contained fewer than 32 hex digits???)")

  return DatasetID(String(buffer))
}

private fun Char.isHex() = when {
  this <= '9' -> this >= '0'
  this <= 'F' -> this >= 'A'
  this <= 'f' -> this >= 'a'
  else        -> false
}
