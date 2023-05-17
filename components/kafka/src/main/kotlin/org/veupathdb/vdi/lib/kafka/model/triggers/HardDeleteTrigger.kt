package org.veupathdb.vdi.lib.kafka.model.triggers

import com.fasterxml.jackson.annotation.JsonProperty
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID

data class HardDeleteTrigger(
  @JsonProperty("userID")
  val userID: UserID,

  @JsonProperty("datasetID")
  val datasetID: DatasetID,
)