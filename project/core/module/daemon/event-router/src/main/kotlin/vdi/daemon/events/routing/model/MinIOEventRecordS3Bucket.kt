package vdi.daemon.events.routing.model

import com.fasterxml.jackson.annotation.JsonProperty

data class MinIOEventRecordS3Bucket(
  @param:JsonProperty("name")
  @field:JsonProperty("name")
  val name: String,

  @param:JsonProperty("ownerIdentity")
  @field:JsonProperty("ownerIdentity")
  val ownerIdentity: MinIOEventUserIdentity,

  @param:JsonProperty("arn")
  @field:JsonProperty("arn")
  val arn: String,
)