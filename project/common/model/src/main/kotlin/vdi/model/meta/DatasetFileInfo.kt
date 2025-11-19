package vdi.model.meta

import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetFileInfo(
  @param:JsonProperty(Name)
  @field:JsonProperty(Name)
  val name: String,

  @param:JsonProperty(Size)
  @field:JsonProperty(Size)
  val size: ULong,
): VersionedMetaObject {
  companion object JsonKey {
    const val Name = "name"
    const val Size = "size"
  }
}