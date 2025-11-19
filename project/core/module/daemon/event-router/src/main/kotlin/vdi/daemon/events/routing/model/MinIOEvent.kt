package vdi.daemon.events.routing.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * MinIO (S3) Event Body
 */
data class MinIOEvent(
  @param:JsonProperty("EventName")
  @field:JsonProperty("EventName")
  val eventType: MinIOEventType,

  @param:JsonProperty("Key")
  @field:JsonProperty("Key")
  val objectKey: String,

  @param:JsonProperty("Records")
  @field:JsonProperty("Records")
  val records: Collection<MinIOEventRecord>
)

