package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty

data class PathogenOrganism(
  @field:JsonProperty(Species)
  val species: String,

  @field:JsonProperty(ReferenceGenome)
  val referenceGenome: String? = null,
) {
  companion object JsonKey {
    const val Species         = "species"
    const val ReferenceGenome = "referenceGenome"
  }
}
