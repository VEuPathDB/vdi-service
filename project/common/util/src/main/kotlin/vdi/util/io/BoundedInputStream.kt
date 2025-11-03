package vdi.util.io

import java.io.IOException
import java.io.InputStream

/**
 * Bounded Input Stream
 *
 * An [InputStream] wrapper that applies a given [limit] to the maximum number
 * of bytes that can be read from the source stream.  If the number of bytes
 * read from the source stream exceeds the configured [limit], an exception will
 * be thrown.  This exception can be customized via a given exception provider
 * function.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 *
 * @constructor Constructs a new `BoundedInputStream` instance.
 *
 * @param stream The target `InputStream` to wrap and apply the limit to.
 *
 * @param limit Maximum number of bytes that may be read from the underlying
 * [stream] before an exception will be thrown.
 *
 * @param errorProvider Function that will be used to provide the exception
 * that is thrown when the maximum byte [limit] is exceeded.
 */
class BoundedInputStream(
  /**
   * Original/underlying stream.
   */
  private val stream: InputStream,

  /**
   * Byte Read Limit
   *
   * Maximum number of bytes that is legal to read from the underlying stream.
   *
   * If this limit is exceeded by reading a number of bytes from this stream
   * that is greater than this limit, an exception will be thrown.
   */
  val limit: ULong,

  /**
   * Error Provider Function
   *
   * This function will be called when the configured [limit] of bytes read is
   * exceeded to provide the exception that will be thrown in that case.
   */
  private val errorProvider: () -> Throwable = { IOException("input stream exceeded max length of $limit") }
): InputStream() {

  /**
   * Current Number of Bytes Read
   *
   * A running total of the number of bytes that have been read from the
   * underlying stream.
   */
  var bytesRead: ULong = 0uL
    private set

  override fun read(): Int =
    stream.read().also { if (it > -1) increaseBytesRead(1u) }

  override fun read(b: ByteArray): Int =
    stream.read(b).also { if (it > 0) increaseBytesRead(it.toULong()) }

  override fun read(b: ByteArray, off: Int, len: Int): Int =
    stream.read(b, off, len).also { if (it > 0) increaseBytesRead(it.toULong()) }

  override fun close() = stream.close()

  private fun increaseBytesRead(by: ULong) {
    bytesRead += by
    if (bytesRead > limit)
      throw errorProvider()
  }
}