package org.veupathdb.service.vdi.service.admin

import org.veupathdb.service.vdi.s3.DatasetStore
import java.io.InputStream
import java.util.stream.Stream

internal fun listAllS3Objects(): InputStream = StreamStream(DatasetStore.streamAllPaths()).buffered()

private class StreamStream(stream: Stream<String>): InputStream() {
  private val iterator = stream.iterator()
  private var current = if (iterator.hasNext()) iterator.next().toByteArray() else null
  private var index = 0

  override fun read(): Int {
    if (currentHasNextByte())
      return nextByte()

    if (tryPrepareNext())
      return 10

    return -1
  }

  private inline fun currentHasNextByte() = current != null && index + 1 < current!!.size

  private inline fun nextByte() = current!![index++].toInt()

  private fun tryPrepareNext(): Boolean {
    index = 0

    return if (iterator.hasNext()) {
      current = iterator.next().toByteArray()
      true
    } else {
      current = null
      false
    }
  }
}