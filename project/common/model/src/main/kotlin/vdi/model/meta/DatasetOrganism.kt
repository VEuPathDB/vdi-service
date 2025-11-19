package vdi.model.meta

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @since v1.7.0
 */
data class DatasetOrganism(
  @param:JsonProperty(Species)
  @field:JsonProperty(Species)
  val species: String,

  @param:JsonProperty(Strain)
  @field:JsonProperty(Strain)
  val strain: String,
) {
  companion object JsonKey {
    const val Species = "species"
    const val Strain  = "strain"
  }
}
