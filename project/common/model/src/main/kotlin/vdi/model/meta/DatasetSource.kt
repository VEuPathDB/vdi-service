package vdi.model.meta

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents an external source where the dataset data is or was available.
 *
 * @since v1.8.0
 */
data class DatasetSource(
  /**
   * URL to the external source.
   */
  @param:JsonProperty(JsonKey.URL)
  @field:JsonProperty(JsonKey.URL)
  val url: String,

  /**
   * Version identifier of the external source.  If the external source does not
   * version its data, this may be the date the data was downloaded.
   */
  @param:JsonProperty(JsonKey.Version)
  @field:JsonProperty(JsonKey.Version)
  val version: String,
) {
  object JsonKey {
    const val URL = "url"
    const val Version = "version"
  }
}
