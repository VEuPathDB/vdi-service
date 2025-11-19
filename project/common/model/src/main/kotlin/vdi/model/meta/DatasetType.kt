package vdi.model.meta

import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetType(
  @param:JsonProperty(JsonKey.Name)
  @field:JsonProperty(JsonKey.Name)
  val name: DataType,

  @param:JsonProperty(JsonKey.Version)
  @field:JsonProperty(JsonKey.Version)
  val version: String,
) {
  override fun toString() = "$name:$version"

  object JsonKey {
    const val Name    = "name"
    const val Version = "version"
  }
}
