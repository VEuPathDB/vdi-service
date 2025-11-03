package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URI

data class LinkedDataset(
  @field:JsonProperty(DatasetURI)
  val datasetURI: URI,

  @field:JsonProperty(SharesRecords)
  val sharesRecords: Boolean,
) {
  companion object JsonKey {
    const val DatasetURI    = "datasetUri"
    const val SharesRecords = "sharesRecords"
  }
}