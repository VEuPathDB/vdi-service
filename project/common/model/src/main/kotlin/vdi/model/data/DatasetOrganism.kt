package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetOrganism(
  @field:JsonProperty(Species)
  val species: String,

  @field:JsonProperty(Strain)
  val strain: String,
) {
  companion object JsonKey {
    const val Species = "species"
    const val Strain  = "strain"
  }
}
