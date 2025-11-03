package vdi.model.data

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetType @JsonCreator constructor(
  @field:JsonProperty(JsonKey.Name)
  val name: DataType,

  @field:JsonProperty(JsonKey.Version)
  val version: String,
) {
  object JsonKey {
    const val Name    = "name"
    const val Version = "version"
  }
}
