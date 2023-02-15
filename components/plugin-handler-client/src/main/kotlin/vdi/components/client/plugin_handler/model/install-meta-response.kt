package vdi.components.client.plugin_handler.model


sealed interface InstallMetaResponse {
  val type: InstallMetaResponseType

  val hasMessage: Boolean

  fun getMessage(): String
}

enum class InstallMetaResponseType { Success, BadRequest, ServerError }

internal object InstallMetaResponseNoContent : InstallMetaResponse {
  override val type = InstallMetaResponseType.Success
  override val hasMessage = false
  override fun getMessage(): String = throw noMessageError()
}

internal class InstallMetaResponseWithMessage(
  override val type: InstallMetaResponseType,
  private val msg: String,
) : InstallMetaResponse {
  override val hasMessage = msg.isNotBlank()
  override fun getMessage(): String = if (hasMessage) msg else throw noMessageError()
}

@Suppress("NOTHING_TO_INLINE")
private inline fun noMessageError() =
  IllegalStateException("called getMessage() on an InstallMetaResponse that has no message")
