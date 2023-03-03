package vdi.components.common.fields

import vdi.components.common.util.byteOf
import vdi.components.common.util.isHexDigit

interface DatasetID {
  override fun toString(): String
}

fun DatasetID(datasetID: String): DatasetID =
  datasetID.toDatasetIDOrNull()
    ?: throw IllegalArgumentException("given string does not resemble a valid dataset ID: $datasetID")

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
