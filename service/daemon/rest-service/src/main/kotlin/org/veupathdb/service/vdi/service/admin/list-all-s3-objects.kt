package org.veupathdb.service.vdi.service.admin

import org.veupathdb.lib.s3.s34k.objects.S3Object
import org.veupathdb.service.vdi.s3.DatasetStore
import org.veupathdb.service.vdi.util.defaultZone
import java.io.InputStream
import java.nio.ByteBuffer
import java.time.OffsetDateTime
import java.util.stream.Stream

internal fun listAllS3Objects(): InputStream = ObjectDetailsStream(DatasetStore.streamAll()).buffered()

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

internal class ObjectDetailsStream(private val stream: Stream<S3Object>): InputStream() {
  private val digitPairs = ByteBuffer.allocate(200)
    .apply {
      for (i in Zero .. Nine) {
        val b = i shl 8
        for (j in Zero .. Nine)
          putShort((b or j).toShort())
      }
      flip()
    }

  private val iterator = stream.iterator()
  private val buffer = ByteBuffer.allocate(1024).flip()

  override fun read(): Int =
    when {
      buffer.hasRemaining() -> buffer.get().toInt()
      tryPrepareNext()      -> read()
      else                  -> -1
    }

  override fun close() = stream.close()

  private fun tryPrepareNext() =
    if (iterator.hasNext()) {
      prepareBuffer(iterator.next())
      true
    } else {
      false
    }

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

  private fun ByteBuffer.writeASCII(value: CharSequence): ByteBuffer {
    for (i in value.indices)
      put(value[i].code.toByte())
    return this
  }

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

  private fun ByteBuffer.writePaddedInt(value: Int, width: Int): ByteBuffer {
    val size = value.stringSize()
    val o = position()
    val p = o + width

    position(p)
    writeInt(value, p)

    var diff = width - size
    while (diff-- > 0) {
      put(o + diff, Zero)
    }

    return this
  }

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
