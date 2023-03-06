package org.veupathdb.service.vdi.util

import java.io.InputStream

class BoundedInputStream(
  private val maxBytes: Long,
  private val inputStream: InputStream,
  private val exceptionProvider: () -> Throwable,
) : InputStream() {
  private var bytesRead = 0L

  override fun read(): Int {
    if (++bytesRead > maxBytes)
      throw exceptionProvider()

    return inputStream.read()
  }

  override fun read(b: ByteArray): Int {
    val red = inputStream.read(b)

    bytesRead += red
    if (bytesRead > maxBytes)
      throw exceptionProvider()

    return red
  }

  override fun read(b: ByteArray, off: Int, len: Int): Int {
    val red = inputStream.read(b, off, len)

    bytesRead += red
    if (bytesRead > maxBytes)
      throw exceptionProvider()

    return red
  }

  override fun close() {
    inputStream.close()
  }
}