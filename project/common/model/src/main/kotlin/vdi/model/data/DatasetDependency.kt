package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetDependency(
  @field:JsonProperty(JsonKey.Identifier)
  val identifier: String,

  @field:JsonProperty(JsonKey.Version)
  val version: String,

  @field:JsonProperty(JsonKey.DisplayName)
  val displayName: String,
) {
  object JsonKey {
    const val Identifier  = "resourceIdentifier"
    const val Version     = "resourceVersion"
    const val DisplayName = "resourceDisplayName"
  }
}
