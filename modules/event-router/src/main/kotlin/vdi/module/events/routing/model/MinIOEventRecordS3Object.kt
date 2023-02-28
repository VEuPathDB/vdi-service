package vdi.module.events.routing.model

import com.fasterxml.jackson.annotation.JsonProperty

data class MinIOEventRecordS3Object(
  @JsonProperty("key")
  val key: String,

  @JsonProperty("size")
  val size: Long?,

  @JsonProperty("eTag")
  val eTag: String?,

  @JsonProperty("contentType")
  val contentType: String?,

  @JsonProperty("userMetadata")
  val userMetadata: Map<String, String>?,

  @JsonProperty("sequencer")
  val sequencer: String,
)