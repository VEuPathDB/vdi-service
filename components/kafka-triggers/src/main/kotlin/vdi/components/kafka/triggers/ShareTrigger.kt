package vdi.components.kafka.triggers

import com.fasterxml.jackson.annotation.JsonProperty

data class ShareTrigger(
  @JsonProperty("userID")
  val userID:    String,

  @JsonProperty("datasetID")
  val datasetID: String,

  @JsonProperty("recipientID")
  val recipientID: String,
)

