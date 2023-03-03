package vdi.components.kafka.triggers

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.components.common.fields.DatasetID
import vdi.components.common.fields.UserID

data class UpdateMetaTrigger(
  @JsonProperty("userID")
  val userID: UserID,

  @JsonProperty("datasetID")
  val datasetID: DatasetID,
)