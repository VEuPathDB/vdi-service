package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URI

data class DatasetHyperlink(
  @field:JsonProperty(JsonKey.URL)
  val url: URI,

  @field:JsonProperty(JsonKey.Text)
  val text: String,

  @field:JsonProperty(JsonKey.Description)
  val description: String? = null,

  @field:JsonProperty(JsonKey.IsPublication)
  val isPublication: Boolean = false,
) {
  object JsonKey {
    const val URL           = "url"
    const val Text          = "text"
    const val Description   = "description"
    const val IsPublication = "isPublication"
  }
}
