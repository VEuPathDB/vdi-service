package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty

data class DOIReference(
  @field:JsonProperty(DOI)
  val doi: String,

  @field:JsonProperty(Description)
  val description: String? = null,
) {
  companion object JsonKey {
    const val DOI         = "doi"
    const val Description = "description"
  }
}