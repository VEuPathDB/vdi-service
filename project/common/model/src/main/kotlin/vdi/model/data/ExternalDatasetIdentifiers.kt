package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty

data class ExternalDatasetIdentifiers(
  @field:JsonProperty(DOIs)
  val dois: Set<DOIReference> = emptySet(),

  @field:JsonProperty(Hyperlinks)
  val hyperlinks: Set<AdditionalHyperlink> = emptySet(),

  @field:JsonProperty(BioprojectIDs)
  val bioprojectIDs: Set<BioprojectIDReference> = emptySet(),
) {
  companion object {
    const val DOIs          = "dois"
    const val Hyperlinks    = "hyperlinks"
    const val BioprojectIDs = "bioprojectIds"
  }
}





