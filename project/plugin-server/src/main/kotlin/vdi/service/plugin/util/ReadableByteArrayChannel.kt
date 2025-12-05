package vdi.service.plugin.util

import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel
import kotlin.math.min

internal class ReadableByteArrayChannel(private val buffer: ByteArray): ReadableByteChannel {
  private var offset = 0

  val remaining: Int
    get() = buffer.size - offset

  override fun read(dst: ByteBuffer): Int {
    val length = min(dst.remaining(), remaining)
    dst.put(buffer, offset, length)
    offset += length
    return length
  }

  override fun isOpen() = remaining > 0

  override fun close() = Unit
}