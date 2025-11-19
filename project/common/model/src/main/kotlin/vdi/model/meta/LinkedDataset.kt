package vdi.model.meta

import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URI

/**
 * Explicitly linked dataset.
 *
 * @since v1.7.0
 */
data class LinkedDataset(
  @param:JsonProperty(DatasetURI)
  @field:JsonProperty(DatasetURI)
  val datasetURI: URI,

  @param:JsonProperty(SharesRecords)
  @field:JsonProperty(SharesRecords)
  val sharesRecords: Boolean,
) {
  companion object JsonKey {
    const val DatasetURI    = "datasetUri"
    const val SharesRecords = "sharesRecords"
  }
}