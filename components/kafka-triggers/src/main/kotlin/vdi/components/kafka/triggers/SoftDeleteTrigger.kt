package vdi.components.kafka.triggers

import com.fasterxml.jackson.annotation.JsonProperty

data class SoftDeleteTrigger(
  @JsonProperty("userID")
  val userID:    String,

  @JsonProperty("datasetID")
  val datasetID: String,
)