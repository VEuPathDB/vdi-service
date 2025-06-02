package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetHyperlink(
  @JsonProperty(JsonKey.URL)
  val url: String,

  @JsonProperty(JsonKey.Text)
  val text: String,

  @JsonProperty(JsonKey.Description)
  val description: String?,

  @JsonProperty(JsonKey.IsPublication)
  val isPublication: Boolean
) {
  object JsonKey {
    const val URL           = "url"
    const val Text          = "text"
    const val Description   = "description"
    const val IsPublication = "isPublication"
  }
}
