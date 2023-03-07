package vdi.components.common.fields

import java.util.UUID
import vdi.components.common.util.byteOf
import vdi.components.common.util.isHexDigit

interface DatasetID {
  override fun toString(): String
}

/**
 * Validates and wraps the given string value as a [DatasetID] instance.
 *
 * If the given string does not appear to be a valid DatasetID then this method
 * will throw an [IllegalArgumentException].
 *
 * @param datasetID ID string to wrap.
 *
 * @return A [DatasetID] instance wrapping the given string value.
 */
fun DatasetID(datasetID: String): DatasetID =
  datasetID.toDatasetIDOrNull()
    ?: throw IllegalArgumentException("given string does not resemble a valid dataset ID: $datasetID")

/**
 * Generates a new, random [DatasetID] instance.
 *
 * @return A new [DatasetID].
 */
fun DatasetID(): DatasetID {
  val uu = UUID.randomUUID().toString()
  return DatasetID(StringBuilder(32)
    .append(uu, 0, 8)
    .append(uu, 9, 13)
    .append(uu, 14, 18)
    .append(uu, 19, 23)
    .append(uu, 24, 36)
    .toString())
}

fun String.toDatasetIDOrNull(): DatasetID? {
  if (length != 32)
    return null

  for (c in this)
    if (!c.isHexDigit())
      return null

  val out = ByteArray(16)
  for (i in 0 until 32 step 2)
    out[i / 2] = byteOf(get(i), get(i + 1))

  return ByteArrayDatasetID(out)
}

@JvmInline
internal value class ByteArrayDatasetID(val value: ByteArray) : DatasetID {
  override fun toString() = String(CharArray(32).also(value::toHexString))
}

@Suppress("NOTHING_TO_INLINE")
private inline fun ByteArray.toHexString(ca: CharArray) {
  var i = 0
  for (b in this) {
    b.toHexString(ca, i)
    i += 2
  }
}

@Suppress("NOTHING_TO_INLINE")
private inline fun Byte.toHexString(ca: CharArray, offset: Int) {
  val i = toInt()
  ca[offset]     = (i.shr(4) and 0xF).toHexDigit()
  ca[offset + 1] = (i and 0xF).toHexDigit()
}

@Suppress("NOTHING_TO_INLINE")
private inline fun Int.toHexDigit() =
  when {
    this < 10 -> '0' + this
    this < 16 -> 'A' + (this - 10)
    else      -> throw IllegalStateException()
  }
