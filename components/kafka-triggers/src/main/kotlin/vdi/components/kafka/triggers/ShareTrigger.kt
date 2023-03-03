package vdi.components.kafka.triggers

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.components.common.fields.DatasetID
import vdi.components.common.fields.UserID

data class ShareTrigger(
  @JsonProperty("userID")
  val userID: UserID,

  @JsonProperty("datasetID")
  val datasetID: DatasetID,

  @JsonProperty("recipientID")
  val recipientID: UserID,
)

