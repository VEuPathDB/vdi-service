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
    if (maxInputSize > MaxSizeToLoadInMemory)
      this.parseAsJson(ByteArray(MaxSizeToLoadInMemory.toInt()), type)
    else
      this.parseAsJson(type)
  }

private fun <T: Any> Source.parseAsJson(buffer: ByteArray, type: KClass<T>): T =
  JSON.readValue(buffer, 0, readAtMostTo(buffer), type.java)

private fun <T: Any> Source.parseAsJson(type: KClass<T>): T =
  JSON.readValue(asInputStream(), type.java)
