package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetDependency(
  @JsonProperty(JsonKey.Identifier)
  val identifier: String,

  @JsonProperty(JsonKey.Version)
  val version: String,

  @JsonProperty(JsonKey.DisplayName)
  val displayName: String,
) {
  object JsonKey {
    const val Identifier  = "resourceIdentifier"
    const val Version     = "resourceVersion"
    const val DisplayName = "resourceDisplayName"
  }
}
