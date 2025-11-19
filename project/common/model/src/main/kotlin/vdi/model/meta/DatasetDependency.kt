package vdi.model.meta

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.model.meta.VersionedMetaObject

data class DatasetDependency(
  @param:JsonProperty(Identifier)
  @field:JsonProperty(Identifier)
  val identifier: String,

  @param:JsonProperty(Version)
  @field:JsonProperty(Version)
  val version: String,

  @param:JsonProperty(DisplayName)
  @field:JsonProperty(DisplayName)
  val displayName: String,
) {
  companion object JsonKey {
    const val Identifier  = "resourceIdentifier"
    const val Version     = "resourceVersion"
    const val DisplayName = "resourceDisplayName"
  }
}
