package vdi.model.meta

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.model.meta.VersionedMetaObject

/**
 * @since v1.7.0
 */
data class BioprojectIDReference(
  @param:JsonProperty(ID)
  @field:JsonProperty(ID)
  val id: String,

  @param:JsonProperty(Description)
  @field:JsonProperty(Description)
  val description: String? = null,
) {
  companion object JsonKey {
    const val ID          = "id"
    const val Description = "description"
  }
}
