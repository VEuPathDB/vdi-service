package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty

data class ExternalDatasetIdentifiers(
  @field:JsonProperty(DOIs)
  val dois: List<DOIReference> = emptyList(),

  @field:JsonProperty(Hyperlinks)
  val hyperlinks: List<DatasetHyperlink> = emptyList(),

  @field:JsonProperty(BioprojectIDs)
  val bioprojectIDs: List<BioprojectIDReference> = emptyList(),
) {
  companion object {
    const val DOIs          = "dois"
    const val Hyperlinks    = "hyperlinks"
    const val BioprojectIDs = "bioprojectIds"
  }
}





