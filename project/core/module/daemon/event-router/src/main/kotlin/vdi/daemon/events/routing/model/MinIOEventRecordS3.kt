package vdi.daemon.events.routing.model

import com.fasterxml.jackson.annotation.JsonProperty

data class MinIOEventRecordS3(
  @JsonProperty("s3SchemaVersion")
  val s3SchemaVersion: String,

  @JsonProperty("configurationId")
  val configurationID: String,

  @JsonProperty("bucket")
  val bucket: MinIOEventRecordS3Bucket,

  @JsonProperty("object")
  val `object`: MinIOEventRecordS3Object,
)