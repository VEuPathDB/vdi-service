package vdi.core.kafka

enum class KafkaCompressionType {
  NONE,
  GZIP,
  SNAPPY,
  LZ4,
  ZSTD,
  ;

  override fun toString() = name.lowercase()

  companion object {
    @JvmStatic
    fun fromString(value: String) =
      fromStringOrNull(value) ?: throw IllegalArgumentException("unrecognized KafkaCompressionType value: $value")

    @JvmStatic
    fun fromStringOrNull(value: String): KafkaCompressionType? =
      value.uppercase().let { up -> entries.firstOrNull { it.name == up } }
  }
}
