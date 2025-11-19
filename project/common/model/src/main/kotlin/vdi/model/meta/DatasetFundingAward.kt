package vdi.model.meta

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @since v1.7.0
 */
data class DatasetFundingAward(
  @param:JsonProperty(Agency)
  @field:JsonProperty(Agency)
  val agency: String,

  @param:JsonProperty(Award)
  @field:JsonProperty(Award)
  val awardNumber: String,
) {
  companion object JsonKey {
    const val Agency = "agency"
    const val Award  = "awardNumber"
  }
}
