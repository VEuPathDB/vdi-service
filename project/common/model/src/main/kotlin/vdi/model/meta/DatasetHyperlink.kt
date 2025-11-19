package vdi.model.meta

import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URI

/**
 * @since v1.7.0
 */
data class DatasetHyperlink(
  @param:JsonProperty(URL)
  @field:JsonProperty(URL)
  val url: URI,

  @param:JsonProperty(Description)
  @field:JsonProperty(Description)
  val description: String? = null,
) {
  companion object JsonKey {
    const val URL         = "url"
    const val Description = "description"
  }
}
