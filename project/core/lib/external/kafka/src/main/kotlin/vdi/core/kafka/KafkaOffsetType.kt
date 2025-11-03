package vdi.core.kafka

enum class KafkaOffsetType {
  Earliest,
  Latest,
  None,
  ;

  override fun toString() = name.lowercase()

  companion object {
    @JvmStatic
    fun fromString(value: String) =
      KafkaOffsetType.Companion.fromStringOrNull(value) ?: throw IllegalArgumentException("unrecognized KafkaOffsetType value: $value")

    @JvmStatic
    fun fromStringOrNull(value: String): KafkaOffsetType? =
      value.lowercase().let { low -> entries.firstOrNull { it.toString() == low } }
  }
}
