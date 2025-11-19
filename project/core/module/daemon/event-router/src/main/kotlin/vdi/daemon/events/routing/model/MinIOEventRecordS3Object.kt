package vdi.daemon.events.routing.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Representation of the S3 record in MinIO event message. I wasn't able to find the structure documented in MinIO
 * docs, but it should be compatible with the S3 structure:
 *
 * https://docs.aws.amazon.com/AmazonS3/latest/userguide/notification-content-structure.html
 */
data class MinIOEventRecordS3Object(
  @param:JsonProperty("key")
  @field:JsonProperty("key")
  val key: String,

  @param:JsonProperty("size")
  @field:JsonProperty("size")
  val size: Long?,

  @param:JsonProperty("eTag")
  @field:JsonProperty("eTag")
  val eTag: String?,

  @param:JsonProperty("contentType")
  @field:JsonProperty("contentType")
  val contentType: String?,

  @param:JsonProperty("userMetadata")
  @field:JsonProperty("userMetadata")
  val userMetadata: Map<String, String>?,

  @param:JsonProperty("sequencer")
  @field:JsonProperty("sequencer")
  val sequencer: String,

  @param:JsonProperty("versionId")
  @field:JsonProperty("versionId")
  val versionId: String?,
)