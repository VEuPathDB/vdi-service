package vdi.daemon.events.routing.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class MinIOEventRecord(
  @param:JsonProperty("eventVersion")
  @field:JsonProperty("eventVersion")
  val eventVersion: String,

  @param:JsonProperty("eventSource")
  @field:JsonProperty("eventSource")
  val eventSource: String,

  @param:JsonProperty("awsRegion")
  @field:JsonProperty("awsRegion")
  val awsRegion: String,

  @param:JsonProperty("eventTime")
  @field:JsonProperty("eventTime")
  val eventTime: OffsetDateTime,

  @param:JsonProperty("eventName")
  @field:JsonProperty("eventName")
  val eventName: String,

  @param:JsonProperty("userIdentity")
  @field:JsonProperty("userIdentity")
  val userIdentity: MinIOEventUserIdentity,

  @param:JsonProperty("requestParameters")
  @field:JsonProperty("requestParameters")
  val requestParameters: Map<String, String>?,

  @param:JsonProperty("responseElements")
  @field:JsonProperty("responseElements")
  val responseElements: Map<String, String>,

  @param:JsonProperty("s3")
  @field:JsonProperty("s3")
  val s3: MinIOEventRecordS3,

  @param:JsonProperty("source")
  @field:JsonProperty("source")
  val source: MinIOEventRecordSource
)