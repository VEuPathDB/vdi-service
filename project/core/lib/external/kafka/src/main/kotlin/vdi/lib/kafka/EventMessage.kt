package vdi.lib.kafka

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.model.data.DatasetID
import vdi.model.data.UserID

data class EventMessage(
  @JsonProperty("userID")
  val userID: UserID,

  @JsonProperty("datasetID")
  val datasetID: DatasetID,

  @JsonProperty("source")
  val eventSource: EventSource,
)
