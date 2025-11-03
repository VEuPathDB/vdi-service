package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetFundingAward(
  @field:JsonProperty(Agency)
  val agency: String,

  @field:JsonProperty(Award)
  val awardNumber: String,
) {
  companion object JsonKey {
    const val Agency = "agency"
    const val Award  = "awardNumber"
  }
}
