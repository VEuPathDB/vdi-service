package vdi.service.rest.util

import java.io.InputStream

/**
 * Bounded Input Stream
 *
 * An input stream wrapper that limits the bytes read to the configured maximum.
 *
 * If the underlying [InputStream] contains more than the configured maximum
 * number of bytes, an exception will be thrown.  The thrown exception is
 * provided by a function given on instance construction.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 *
 * @constructor Constructs a new [BoundedInputStream] instance.
 *
 * @param maxBytes The maximum number of bytes that may be read from the
 * underlying input stream before an exception will be thrown.
 *
 * @param inputStream The underlying input stream that will be read from.
 *
 * @param exceptionProvider A provider function for the exception to throw in
 * the event that the underlying input stream contains more than the configured
 * max number of bytes.
 */
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
