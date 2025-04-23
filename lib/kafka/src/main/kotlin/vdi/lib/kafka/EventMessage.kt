package vdi.lib.kafka

import com.fasterxml.jackson.annotation.JsonProperty
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID

data class EventMessage(
  @JsonProperty("userID")
  val userID: UserID,

  @JsonProperty("datasetID")
  val datasetID: DatasetID,

  @JsonProperty("source")
  val eventSource: EventSource,
)
