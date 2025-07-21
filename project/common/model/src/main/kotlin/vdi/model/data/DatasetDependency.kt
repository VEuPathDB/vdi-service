package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetDependency(
  @field:JsonProperty(Identifier)
  val identifier: String,

  @field:JsonProperty(Version)
  val version: String,

  @field:JsonProperty(DisplayName)
  val displayName: String,
) {
  companion object JsonKey {
    const val Identifier  = "resourceIdentifier"
    const val Version     = "resourceVersion"
    const val DisplayName = "resourceDisplayName"
  }
}
