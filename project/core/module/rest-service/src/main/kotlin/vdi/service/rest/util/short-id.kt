@file:Suppress("SameParameterValue")
package vdi.service.rest.util

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random
import vdi.model.OriginTimestamp

/**
 * VDI Short ID Generator
 *
 * Implementation of VDI-specific dataset ID generation.  This ID generator is
 * not suitable for general purpose use; it operates on VDI specifics at a time
 * resolution in seconds.
 */
object ShortID {
  /**
   * Maximum allowed value for tie-breaking before resetting the counter for the
   * next timestamp second.
   *
   * `31 == 0001_1111` which is expanded via [chunkShift] to the max base62
   * alphabet index 61 (`0011_1101`).
   */
  private const val TieMax: Byte = 31

  /**
   * Cap on generated random int values to keep those values within 30 bits for
   * lossless serialization, which significantly reduces the chance of conflict.
   */
  private const val MaxRandomInt = 1.shl(30).minus(1)

  /**
   * Base62 Alphabet
   */
  private val alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".encodeToByteArray()

  /**
   * ID component byte ordering.
   *
   * Controls what parts of the generated ID appear in what position in the
   * output string with each array element representing the string character
   * index.
   *
   * **WARNING**: These values have been intentionally organized by hand to
   * control the output ID structure.  This ordering MUST NOT be changed in
   * order to maintain uniqueness against previously generated values.
   */
  private val ByteOrder = intArrayOf(
    // tie-breaker
    10,
    // timestamp (least -> most volatile)
    7,
    8,
    11,
    6,
    2,
    3,
    // random int
    0,
    12,
    4,
    1,
    9,
    5,
  )

  /**
   * "Offset" used for current timestamps based on the VDI project origin
   * timestamp.
   *
   * Generated timestamps are reduced by this value to get the count of seconds
   * from VDI origin rather than the count of seconds from the Unix epoch.
   *
   * The purpose of the value is a constantly incrementing value based on a
   * fixed point, and using the project origin instead of the Unix epoch
   * accomplishes that goal while reducing value well into the uint32 range,
   * allowing timestamps to be safely tracked in 4 bytes rather than 8 for more
   * than 100 years.
   */
  private val TimestampOffset = OriginTimestamp.toEpochSecond()

  /**
   * Random number generator.
   */
  private val random = Random(System.nanoTime())

  /**
   * Mutex used for threadsafe time updates and tie-breaker increments.
   */
  private val lock = Mutex()

  /**
   * Current tie-breaking value.
   *
   * This value is included in the generated output IDs and is incremented for
   * each ID generated within a single system-clock second.
   */
  private var tieBreaker: Byte = 0

  /**
   * Timestamp in seconds of when the last ID was generated.
   */
  private var lastTime = timestamp()

  /**
   * Generates a 13 digit time-based identifier encoded in base-62.
   *
   * This identifier is constructed of the following parts:
   *
   * 1. Counter of seconds since [OriginTimestamp].
   * 2. [Tie-breaker][tieBreaker] value for handling multiple ID requests within
   *    a single system-clock second.
   * 3. A random 30-bit int value.
   *
   * @return A new identifier string.
   */
  suspend fun generate(): String {
    var curTime = timestamp()
    var tbCopy: Byte

    lock.withLock {
      if (curTime <= lastTime) {
        if (tieBreaker == TieMax) {
          lastTime++
          curTime = lastTime
          tieBreaker = 0
        } else {
          tieBreaker++
        }
      } else {
        lastTime = curTime
        tieBreaker = 0
      }

      tbCopy = tieBreaker
    }

    return String(buildIdentifier(curTime, tbCopy))
  }

  private fun buildIdentifier(timestamp: UInt, tieBreaker: Byte): ByteArray {
    val tsi = timestamp.toInt() // this may flip the int to negative, use ushr
    val out = random.nextBytes(ByteArray(13))
    var offset = 0

    // [0..<1]
    offset += tieBreaker.toInt().chunkShiftInto(1, out, offset)

    // [1..<7] -- 6 'chunks' of 5 bytes gives us 30 bits, which is enough space
    // to prevent rollover for 30 years.
    offset += tsi.chunkShiftInto(6, out, offset)

    // [7..<14]
    random.nextInt(MaxRandomInt).chunkShiftInto(6, out, offset)

    return out
  }

  private fun timestamp() = System.currentTimeMillis()
    .div(1000)              // to seconds
    .minus(TimestampOffset) // subtract origin timestamp
    .toUInt()

  /**
   * Masks and shifts the last 5 bits of the receiver int into the spacing used
   * by the base62 alphabet index cap of 61 by shifting the highest 4 bits to
   * the left by one.
   *
   * Output int requires 6 bits.
   *
   * ```
   * 1111_1111 -> 0001_1111 -> 0011_1101
   * ```
   *
   * Mask `30` == `0001_1110`
   */
  private fun Int.chunkShift(): Int =
    and(1).or(and(30).shl(1))

  private fun Int.chunkShiftInto(chunks: Int, target: ByteArray, offset: Int): Int {
    repeat(chunks) { target[ByteOrder[it + offset]] = alphabet[ushr(it * 5).chunkShift()] }
    return chunks
  }
}
