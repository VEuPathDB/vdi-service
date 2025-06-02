package vdi.util.zip

/**
 * Zip compression level.
 *
 * Must be a value from 0-9.
 *
 * @since 6.3.0
 */
@JvmInline
value class CompressionLevel(val value: UByte) {
  init {
    if (value > 9u) {
      throw IllegalArgumentException("Zip level must be 0-9, $value was given.")
    }
  }
}