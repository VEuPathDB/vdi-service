package vdi.module.events.routing.model

import com.fasterxml.jackson.annotation.JsonProperty

data class MinIOEventRecordS3Bucket(
  @JsonProperty("name")
  val name: String,

  @JsonProperty("ownerIdentity")
  val ownerIdentity: MinIOEventUserIdentity,

  @JsonProperty("arn")
  val arn: String,
)