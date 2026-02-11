package vdi.model.misc

import com.fasterxml.jackson.annotation.JsonCreator

data class UploadErrorReport
@JsonCreator
constructor(
  val message: String,
  val stacktrace: List<ErrorTraceElement>,
) {
  constructor(msg: String, ex: Throwable): this(
    msg,
    ex.stackTrace.map(::ErrorTraceElement)
  )

  constructor(ex: Throwable): this(ex.message ?: ex::class.simpleName!!, ex)

  constructor(msg: String): this(msg, emptyList())
}

