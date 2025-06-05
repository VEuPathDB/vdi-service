package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetHyperlink(
  @field:JsonProperty(JsonKey.URL)
  val url: String,

  @field:JsonProperty(JsonKey.Text)
  val text: String,

  @field:JsonProperty(JsonKey.Description)
  val description: String?,

  @field:JsonProperty(JsonKey.IsPublication)
  val isPublication: Boolean
) {
  object JsonKey {
    const val URL           = "url"
    const val Text          = "text"
    const val Description   = "description"
    const val IsPublication = "isPublication"
  }
}
