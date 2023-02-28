package vdi.module.events.routing.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * MinIO (S3) Event Body
 */
data class MinIOEvent(
  @JsonProperty("EventName")
  val eventType: MinIOEventType,

  @JsonProperty("Key")
  val objectKey: String,

  @JsonProperty("Records")
  val records: Collection<MinIOEventRecord>
)

