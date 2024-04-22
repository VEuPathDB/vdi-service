package vdi.component.kafka

enum class KafkaOffsetType {
  EARLIEST,
  LATEST,
  NONE,
  ;

  override fun toString() = name.lowercase()

  companion object {
    @JvmStatic
    fun fromString(value: String) =
      fromStringOrNull(value) ?: throw IllegalArgumentException("unrecognized KafkaOffsetType value: $value")

    @JvmStatic
    fun fromStringOrNull(value: String): KafkaOffsetType? {
      val value = value.lowercase()

      for (enum in values())
        if (value == enum.toString())
          return enum

      return null
    }
  }
}