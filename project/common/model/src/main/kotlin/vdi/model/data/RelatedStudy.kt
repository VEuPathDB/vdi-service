package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URI

data class RelatedStudy(
  @field:JsonProperty(StudyURI)
  val studyURI: URI,

  @field:JsonProperty(SharesRecords)
  val sharesRecords: Boolean,
) {
  companion object JsonKey {
    const val StudyURI      = "studyUri"
    const val SharesRecords = "sharesRecords"
  }
}