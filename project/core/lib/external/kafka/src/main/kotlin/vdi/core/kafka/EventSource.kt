package vdi.core.kafka

import com.fasterxml.jackson.annotation.JsonCreator

enum class EventSource {
  ObjectStore,
  FullReconciler,
  SlimReconciler,
  UpdateMetaLane,
  InstallDataLane,
  ;

  companion object {
    @JvmStatic
    @JsonCreator
    fun fromString(value: String): EventSource {
      for (enum in entries)
        if (enum.name == value)
          return enum

      throw IllegalArgumentException("unrecognized EventSource value: $value")
    }
  }
}
