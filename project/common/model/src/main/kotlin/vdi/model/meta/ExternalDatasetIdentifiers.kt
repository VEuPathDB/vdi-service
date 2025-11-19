package vdi.model.meta

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @since v1.7.0
 */
data class ExternalDatasetIdentifiers(
  @param:JsonProperty(DOIs)
  @field:JsonProperty(DOIs)
  val dois: List<DOIReference> = emptyList(),

  @param:JsonProperty(Hyperlinks)
  @field:JsonProperty(Hyperlinks)
  val hyperlinks: List<DatasetHyperlink> = emptyList(),

  @param:JsonProperty(BioprojectIDs)
  @field:JsonProperty(BioprojectIDs)
  val bioprojectIDs: List<BioprojectIDReference> = emptyList(),
) {
  companion object {
    const val DOIs          = "dois"
    const val Hyperlinks    = "hyperlinks"
    const val BioprojectIDs = "bioprojectIds"
  }
}
