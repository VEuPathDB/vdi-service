package vdi.model.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URI

@JsonIgnoreProperties("text", "isPublication")
data class DatasetHyperlink(
  @field:JsonProperty(URL)
  val url: URI,

  @field:JsonProperty(Description)
  val description: String? = null,
) {
  companion object JsonKey {
    const val URL         = "url"
    const val Description = "description"
  }
}
