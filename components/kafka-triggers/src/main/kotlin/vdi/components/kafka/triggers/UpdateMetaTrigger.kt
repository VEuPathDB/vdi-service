package vdi.components.kafka.triggers

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateMetaTrigger(
  @JsonProperty("userID")
  val userID:    String,

  @JsonProperty("datasetID")
  val datasetID: String,
)