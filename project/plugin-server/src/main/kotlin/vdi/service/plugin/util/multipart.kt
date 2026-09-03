package vdi.service.plugin.util

import io.ktor.http.content.PartData
import io.ktor.utils.io.asSource
import kotlinx.io.Source
import kotlinx.io.asInputStream
import kotlinx.io.asSource
import kotlinx.io.buffered
import kotlin.reflect.KClass
import vdi.json.JSON

private const val MaxSizeToLoadInMemory = 16384uL

inline fun <reified T: Any> PartData.parseAsJson(maxInputSize: ULong): T =
  parseAsJson(maxInputSize, T::class)

fun <T: Any> PartData.parseAsJson(maxInputSize: ULong, type: KClass<T>): T =
  with(
    when (this) {
      is PartData.BinaryChannelItem -> provider().asSource().buffered()
      is PartData.BinaryItem        -> provider()
      is PartData.FileItem          -> provider().asSource().buffered()
      is PartData.FormItem          -> value.byteInputStream().asSource().buffered()
    }
  ) {
//    if (maxInputSize > MaxSizeToLoadInMemory)
//      this.parseAsJson(ByteArray(MaxSizeToLoadInMemory.toInt()), type)
//    else
      this.parseAsJson(type)
  }

private fun <T: Any> Source.parseAsJson(buffer: ByteArray, type: KClass<T>): T =
  JSON.readValue(buffer, 0, repeatedReadAtMostTo(buffer), type.java)

private fun <T: Any> Source.parseAsJson(type: KClass<T>): T =
  JSON.readValue(asInputStream(), type.java)

/**
 * Repeatedly calls [readAtMostTo] until the buffer is full or
 *
 * Depending on the underlying [Source] implementation, the [readAtMostTo]
 * method may cap itself at 8KiB regardless of the buffer array size.
 */
private fun Source.repeatedReadAtMostTo(buffer: ByteArray): Int {
  var totalRead = 0

  while (totalRead < buffer.size) {
    val singleRead = readAtMostTo(buffer, totalRead)

    if (singleRead < 0) {
      // if we got a -1 back from `readAtMostTo`, but never actually got any
      // bytes for our buffer, pass up the -1.  Otherwise, break and return the
      // total we did read.
      if (totalRead == 0)
        return -1

      break
    }

    totalRead += singleRead
  }

  return totalRead
}