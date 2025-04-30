package vdi.daemon.events.routing.model

import com.fasterxml.jackson.annotation.JsonCreator

/**
 * Represents a MinIO (S3) event type string's components.
 */
data class MinIOEventType(
  val action: MinIOEventAction,
  val subAction: MinIOEventSubAction
) {
  companion object {
    @JvmStatic
    @JsonCreator
    fun fromString(value: String) =
      when (value) {
        "s3:ObjectCreated:Put"
        -> MinIOEventType(MinIOEventAction.CREATE, MinIOEventCreateSubAction.PUT)

        "s3:ObjectCreated:Post"
        -> MinIOEventType(MinIOEventAction.CREATE, MinIOEventCreateSubAction.POST)

        "s3:ObjectCreated:Copy"
        -> MinIOEventType(MinIOEventAction.CREATE, MinIOEventCreateSubAction.COPY)

        "s3:ObjectCreated:CompleteMultipartUpload"
        -> MinIOEventType(MinIOEventAction.CREATE, MinIOEventCreateSubAction.COMPLETE_MULTIPART_UPLOAD)

        "s3:ObjectRemoved:Delete"
        -> MinIOEventType(MinIOEventAction.DELETE, MinIOEventDeleteSubAction.DELETE)

        "s3:ObjectRemoved:DeleteMarkerCreated"
        -> MinIOEventType(MinIOEventAction.DELETE, MinIOEventDeleteSubAction.DELETE_MARKER_CREATED)

        else
        -> throw IllegalArgumentException("unrecognized MinIOEventType value: $value")
      }
  }
}