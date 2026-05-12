package vdi.model.misc

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonProperty
import vdi.model.DatasetUploadStatus

data class UploadErrorReport @JsonCreator constructor(
  @get:JsonGetter(JsonKey.Status)
  @param:JsonProperty(JsonKey.Status)
  val status: DatasetUploadStatus = DatasetUploadStatus.Failed,

  @get:JsonGetter(JsonKey.Message)
  @param:JsonProperty(JsonKey.Message)
  val message: String,

  @get:JsonGetter(JsonKey.Stacktrace)
  @param:JsonProperty(JsonKey.Stacktrace)
  val stacktrace: List<ErrorTraceElement> = emptyList(),
) {
  constructor(status: DatasetUploadStatus, msg: String, ex: Throwable): this(
    status,
    msg,
    ex.stackTrace.map(::ErrorTraceElement),
  )

  constructor(status: DatasetUploadStatus, ex: Throwable): this(
    status,
    ex.message ?: ex::class.simpleName!!,
    ex,
  )

  constructor(status: DatasetUploadStatus, msg: String): this(
    status,
    msg,
    emptyList(),
  )

  object JsonKey {
    const val Status     = "status"
    const val Message    = "message"
    const val Stacktrace = "stacktrace"
  }
}

