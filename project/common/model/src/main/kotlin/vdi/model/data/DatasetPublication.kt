package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetPublication(
  @field:JsonProperty(JsonKey.PubMedID)
  val pubmedID: String,

  @field:JsonProperty(JsonKey.Citation)
  val citation: String?,

  @field:JsonProperty(JsonKey.IsPrimary)
  val isPrimary: Boolean,
) {
  object JsonKey {
    const val Citation = "citation"
    const val PubMedID = "pubmedId"
    const val IsPrimary = "isPrimary"
  }
}
