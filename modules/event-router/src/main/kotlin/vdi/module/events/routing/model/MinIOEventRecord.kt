package vdi.module.events.routing.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class MinIOEventRecord(
  @JsonProperty("eventVersion")
  val eventVersion: String,

  @JsonProperty("eventSource")
  val eventSource: String,

  @JsonProperty("awsRegion")
  val awsRegion: String,

  @JsonProperty("eventTime")
  val eventTime: OffsetDateTime,

  @JsonProperty("eventName")
  val eventName: String,

  @JsonProperty("userIdentity")
  val userIdentity: MinIOEventUserIdentity,

  @JsonProperty("requestParameters")
  val requestParameters: Map<String, String>,

  @JsonProperty("responseElements")
  val responseElements: Map<String, String>,

  @JsonProperty("s3")
  val s3: MinIOEventRecordS3,

  @JsonProperty("source")
  val source: MinIOEventRecordSource
)