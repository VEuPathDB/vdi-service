package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetPublication(
  @JsonProperty(JsonKey.PubMedID)
  val pubmedID: String,

  @JsonProperty(JsonKey.Citation)
  val citation: String?,

  @JsonProperty(JsonKey.IsPrimary)
  val isPrimary: Boolean,
) {
  object JsonKey {
    const val Citation = "citation"
    const val PubMedID = "pubmedId"
    const val IsPrimary = "isPrimary"
  }
}
