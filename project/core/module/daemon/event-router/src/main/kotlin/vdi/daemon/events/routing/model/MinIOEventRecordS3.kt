package vdi.daemon.events.routing.model

import com.fasterxml.jackson.annotation.JsonProperty

data class MinIOEventRecordS3(
  @param:JsonProperty("s3SchemaVersion")
  @field:JsonProperty("s3SchemaVersion")
  val s3SchemaVersion: String,

  @param:JsonProperty("configurationId")
  @field:JsonProperty("configurationId")
  val configurationID: String,

  @param:JsonProperty("bucket")
  @field:JsonProperty("bucket")
  val bucket: MinIOEventRecordS3Bucket,

  @param:JsonProperty("object")
  @field:JsonProperty("object")
  val `object`: MinIOEventRecordS3Object,
)