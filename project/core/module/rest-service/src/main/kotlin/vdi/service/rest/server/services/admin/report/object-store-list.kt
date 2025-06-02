package vdi.service.rest.server.services.admin.report

import jakarta.ws.rs.core.StreamingOutput
import org.veupathdb.lib.s3.s34k.objects.S3Object
import java.io.InputStream
import java.nio.ByteBuffer
import java.time.OffsetDateTime
import java.util.stream.Stream
import kotlin.math.max
import vdi.service.rest.generated.resources.AdminReports
import vdi.service.rest.s3.DatasetStore
import vdi.service.rest.util.defaultZone

fun listAllS3Objects(): AdminReports.GetAdminReportsObjectStoreListAllResponse =
  AdminReports.GetAdminReportsObjectStoreListAllResponse
    .respond200WithTextPlain(StreamingOutput { out ->
      ObjectDetailsStream(DatasetStore.streamAll()).buffered().use { it.transferTo(out) }
    })

private const val Tab: Byte = 9
private const val NL: Byte = 10
private const val Plus: Byte = 43
private const val Dash: Byte = 45
private const val Dot: Byte = 46
private const val Zero: Byte = 48
private const val Nine: Byte = 57
private const val Colon: Byte = 58
private const val UpperT: Byte = 84
private const val UpperZ: Byte = 90

/**
 * ObjectDetailsStream implements an [InputStream] and produces a TSV whose rows
 * each represent an object present in the S3 bucket VDI is connected to.
 *
 * The columns for the output TSV are:
 * 1. Object path/key
 * 2. Object size in bytes
 * 3. Object last-modified timestamp (rfc3339)
 */
internal class ObjectDetailsStream(private val stream: Stream<S3Object>): InputStream() {
  /**
   * A cache of ASCII digit pairs that represent the numbers 0-99.  These are
   * pre-calculated to reduce the number of iterations necessary to decompose
   * large integer values and stringify them by one order of magnitude.
   */
  private val digitPairs = ByteBuffer.allocate(200)
    .apply {
      for (i in Zero .. Nine) {
        val b = i shl 8
        for (j in Zero .. Nine)
          putShort((b or j).toShort())
      }
      flip()
    }

  /**
   * Iterator over S3 objects to render for consumption by users of this
   * `InputStream` implementation.
   */
  private val s3Objects = stream.iterator()

  /**
   * Temporary buffer for holding the next row to be rendered for consumption
   * by users of this `InputStream`.
   */
  private val buffer = ByteBuffer.allocate(1024).flip()

  override fun read(): Int =
    when {
      buffer.hasRemaining() -> buffer.get().toInt()
      tryPrepareNext()      -> read()
      else                  -> -1
    }

  override fun close() = stream.close()

  /**
   * Attempts to prepare the [buffer] with another row representing the next S3
   * object.
   *
   * @return `true` if another row has been written to the [buffer]; `false` if
   * no additional objects are available.
   */
  private fun tryPrepareNext() =
    if (s3Objects.hasNext()) {
      prepareBuffer(s3Objects.next())
      true
    } else {
      false
    }

  /**
   * Writes the given S3 object as a TSV row to the [buffer].
   *
   * @param next S3 object for the row to write.
   */
  private fun prepareBuffer(next: S3Object) {
    buffer.clear()
      .writeASCII(next.path)
      .put(Tab)
      .writeLong(next.size)
      .put(Tab)
      .also { next.lastModified?.defaultZone()?.run { it.writeDate(this) } }
      .put(NL)
      .flip()
  }

  /**
   * Writes the given string to the target [ByteBuffer] as if it was a stream of
   * ASCII characters.
   *
   * This should always be true as the only strings we are printing are dates,
   * numbers, and paths, and VDI does not use user input for object names.
   *
   * **WARNING**: This method makes no attempt to check whether there is size in
   * the buffer to contain the given string.  That check should be performed by
   * callers before invoking this method.
   *
   * @receiver The `ByteBuffer` into which the string should be written.
   *
   * @param value ASCII string to write to the buffer.
   *
   * @return The receiver instance.
   */
  private fun ByteBuffer.writeASCII(value: CharSequence): ByteBuffer {
    for (i in value.indices)
      put(value[i].code.toByte())
    return this
  }

  /**
   * Writes the given timestamp to the target [ByteBuffer] in RFC3339 format.
   *
   * @receiver The `ByteBuffer` into which the string should be written.
   *
   * @param value Timestamp to write.
   *
   * @return The receiver instance.
   */
  private fun ByteBuffer.writeDate(value: OffsetDateTime): ByteBuffer {
    return writeInt(value.year)
      .put(Dash)
      .writePaddedInt(value.monthValue, 2)
      .put(Dash)
      .writePaddedInt(value.dayOfMonth, 2)
      .put(UpperT)
      .writePaddedInt(value.hour, 2)
      .put(Colon)
      .writePaddedInt(value.minute, 2)
      .put(Colon)
      .writePaddedInt(value.second, 2)
      .put(Dot)
      .writePaddedInt(value.nano / 1_000_000, 3)
      .writeZoneOffset(value)
  }

  private fun ByteBuffer.writeLong(value: Long): ByteBuffer {
    var l = value
    var pos = position() + value.stringSize()
    position(pos)

    while (l > Int.MAX_VALUE) {
      pos -= 2
      putShort(pos, digitPairs.getShort((l % 100).toInt() * 2))
      l /= 100
    }

    return writeInt(l.toInt(), pos)
  }

  private fun ByteBuffer.writeInt(value: Int): ByteBuffer {
    val p = position() + value.stringSize()
    position(p)
    return writeInt(value, p)
  }

  /**
   * Writes the given `Int` value to the target [ByteBuffer] left-padded to the
   * given width with zero characters.
   *
   * If the given value's string width is greater than or equal to the target
   * width, no padding will be written.
   *
   * **Examples**
   * ```kt
   * buffer.writePaddedInt(value = 3, width = 5) // 00003
   *
   * buffer.writePaddedInt(value = 100, width = 2) // 100
   * ```
   *
   * @receiver The `ByteBuffer` into which the string should be written.
   *
   * @param value Value to be written to the buffer.
   *
   * @param width Target width for the padded number string.
   *
   * @return The receiver instance.
   */
  private fun ByteBuffer.writePaddedInt(value: Int, width: Int): ByteBuffer {
    val size = value.stringSize()
    val o = position()
    val p = o + max(width, size)

    position(p)
    writeInt(value, p)

    var diff = width - size
    while (diff-- > 0) {
      put(o + diff, Zero)
    }

    return this
  }

  /**
   * Writes the given `Int` value to the target [ByteBuffer] starting from the
   * given start position and working backward.
   *
   * **WARNING**: The [startingPosition] value is the position where this method
   * will start when writing the stringified int value, it is **NOT** the
   * starting position of the int from a left-to-right reading perspective.
   *
   * @receiver The `ByteBuffer` into which the string should be written.
   *
   * @param value Value to be written to the buffer.
   *
   * @param startingPosition The position where this method will start when
   * writing to the buffer.  This method will work backwards from this position.
   *
   * @return The receiver instance.
   */
  private fun ByteBuffer.writeInt(value: Int, startingPosition: Int): ByteBuffer {
    var i = value
    var p = startingPosition

    while (i >= 100) {
      p -= 2
      putShort(p, digitPairs.getShort((i % 100) * 2))
      i /= 100
    }

    if (i > 9)
      putShort(p-2, digitPairs.getShort(i * 2))
    else
      put(p-1, (Zero + i).toByte())

    return this
  }

  /**
   * Writes the zone-offset string for the given timestamp to the target
   * [ByteBuffer] in RFC3339 format ('00:00' or 'Z').
   *
   * @receiver The `ByteBuffer` into which the string should be written.
   *
   * @param value Timestamp whose zone offset should be written to the buffer.
   *
   * @return The receiver instance.
   */
  private fun ByteBuffer.writeZoneOffset(value: OffsetDateTime): ByteBuffer {
    var seconds = value.offset.totalSeconds

    when {
      seconds == 0 -> return put(UpperZ)
      seconds >  0 -> put(Plus)
      else         -> {
        put(Dash)
        seconds = -seconds
      }
    }

    val hours = (seconds/60).toFloat()/60F

    return writePaddedInt(hours.toInt(), 2)
      .put(Colon)
      .writePaddedInt((hours % 1F * 60F).toInt(), 2)
  }
}

/**
 * Determines how many ASCII characters are necessary to render the target
 * `Long` value as a string.
 *
 * @receiver Value whose string length should be calculated.
 *
 * @return The number of ASCII characters needed to represent the target value.
 */
private fun Long.stringSize(): Int {
  var t = this
  var s = 0

  while (t > 1000) {
    s += 3
    t /= 1000
  }

  return when {
    t > 99 -> s + 3
    t > 9  -> s + 2
    else   -> s + 1
  }
}

/**
 * Determines how many ASCII characters are necessary to render the target `Int`
 * value as a string.
 *
 * @receiver Value whose string length should be calculated.
 *
 * @return The number of ASCII characters needed to represent the target value.
 */
private fun Int.stringSize(): Int {
  var t = this
  var s = 0

  while (t > 1000) {
    s += 3
    t /= 1000
  }

  return when {
    t > 99 -> s + 3
    t > 9  -> s + 2
    else   -> s + 1
  }
}
