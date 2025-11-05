package vdi.core.kafka

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.model.EventID
import vdi.model.data.DatasetID
import vdi.model.data.UserID

data class EventMessage(
  @get:JsonProperty(EventID)
  @param:JsonProperty(EventID)
  val eventID: EventID,

  @get:JsonProperty(UserID)
  @param:JsonProperty(UserID)
  val userID: UserID,

  @get:JsonProperty(DatasetID)
  @param:JsonProperty(DatasetID)
  val datasetID: DatasetID,

  @get:JsonProperty(Source)
  @param:JsonProperty(Source)
  val eventSource: EventSource,
) {
  private companion object JsonKey {
    const val DatasetID = "datasetID"
    const val EventID = "eventID"
    const val Source = "source"
    const val UserID = "userID"
  }
}
