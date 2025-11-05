package vdi.model.data

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetType @JsonCreator constructor(
  @param:JsonProperty(JsonKey.Name)
  @field:JsonProperty(JsonKey.Name)
  val name: DataType,

  @param:JsonProperty(JsonKey.Version)
  @field:JsonProperty(JsonKey.Version)
  val version: String,
) {
  object JsonKey {
    const val Name    = "name"
    const val Version = "version"
  }
}
