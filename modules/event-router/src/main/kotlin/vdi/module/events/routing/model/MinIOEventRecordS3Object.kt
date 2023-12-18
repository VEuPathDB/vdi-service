package vdi.module.events.routing.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Representation of the S3 record in MinIO event message. I wasn't able to find the structure documented in MinIO
 * docs, but it should be compatible with the S3 structure:
 *
 * https://docs.aws.amazon.com/AmazonS3/latest/userguide/notification-content-structure.html
 */
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

  @JsonProperty("versionId")
  val versionId: String?,
)