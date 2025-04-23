package vdi.lib.kafka

enum class KafkaOffsetType {
  EARLIEST,
  LATEST,
  NONE,
  ;

  override fun toString() = name.lowercase()

  companion object {
    @JvmStatic
    fun fromString(value: String) =
      vdi.lib.kafka.KafkaOffsetType.Companion.fromStringOrNull(value) ?: throw IllegalArgumentException("unrecognized KafkaOffsetType value: $value")

    @JvmStatic
    fun fromStringOrNull(value: String): vdi.lib.kafka.KafkaOffsetType? {
      val value = value.lowercase()

      for (enum in values())
        if (value == enum.toString())
          return enum

      return null
    }
  }
}
