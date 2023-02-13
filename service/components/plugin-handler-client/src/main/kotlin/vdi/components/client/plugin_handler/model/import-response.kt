package vdi.components.client.plugin_handler.model

import java.io.InputStream


interface ImportResponse {
  val type: ImportResponseType

  val hasMessage: Boolean

  fun getMessage(): String

  val hasWarnings: Boolean

  fun getWarnings(): Collection<String>

  val hasPayload: Boolean

  fun getPayload(): InputStream
}


enum class ImportResponseType {
  Success,
  BadRequest,
  ValidationFailure,
  TransformationFailure,
  ServerError,
}


internal class ImportResponseWithWarnings(
  override val type: ImportResponseType,
  private val warns: Collection<String>
): ImportResponse {
  override val hasMessage = false
  override val hasWarnings = warns.isNotEmpty()
  override val hasPayload = false

  override fun getMessage(): String = throw noMessageError()
  override fun getWarnings(): Collection<String> = if (hasWarnings) warns else throw noWarningsError()
  override fun getPayload(): InputStream = throw noPayloadError()
}


internal class ImportResponseWithMessage(
  override val type: ImportResponseType,
  private val mess: String,
) : ImportResponse {
  override val hasMessage = mess.isNotBlank()
  override val hasWarnings = false
  override val hasPayload = false

  override fun getMessage(): String = if (hasMessage) mess else throw noMessageError()
  override fun getWarnings(): Collection<String> = throw noWarningsError()
  override fun getPayload(): InputStream = throw noPayloadError()
}


internal class ImportResponseWithPayload(private val stream: InputStream) : ImportResponse {
  override val type = ImportResponseType.Success
  override val hasMessage = false
  override val hasWarnings = false
  override val hasPayload = true

  override fun getMessage(): String = throw noMessageError()
  override fun getWarnings(): Collection<String> = throw noWarningsError()
  override fun getPayload(): InputStream = stream
}


@Suppress("NOTHING_TO_INLINE")
private inline fun noMessageError() =
  IllegalStateException("called getMessage() on an ImportResponse that has no message")

@Suppress("NOTHING_TO_INLINE")
private inline fun noWarningsError() =
  IllegalStateException("called getWarnings() on an ImportResponse that has no warnings")

private inline fun noPayloadError() =
  IllegalStateException("called getPayload() on an ImportResponse that has no payload")