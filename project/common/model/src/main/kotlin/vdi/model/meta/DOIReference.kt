package vdi.model.meta

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @since v1.7.0
 */
data class DOIReference(
  @param:JsonProperty(DOI)
  @field:JsonProperty(DOI)
  val doi: String,

  @param:JsonProperty(Description)
  @field:JsonProperty(Description)
  val description: String? = null,
) {
  companion object JsonKey {
    const val DOI         = "doi"
    const val Description = "description"
  }
}