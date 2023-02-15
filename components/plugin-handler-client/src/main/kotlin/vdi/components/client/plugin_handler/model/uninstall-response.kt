package vdi.components.client.plugin_handler.model


sealed interface UninstallResponse {
  val type: UninstallResponseType

  val hasMessage: Boolean

  fun getMessage(): String
}

enum class UninstallResponseType {
  Success,
  BadRequest,
  ServerError,
}

internal object UninstallResponseNoContent : UninstallResponse {
  override val type = UninstallResponseType.Success
  override val hasMessage = false
  override fun getMessage(): String = throw noMessageError()
}

internal class UninstallResponseWithMessage(
  override val type: UninstallResponseType,
  private val msg: String,
) : UninstallResponse {
  override val hasMessage = msg.isNotBlank()
  override fun getMessage(): String = if (hasMessage) msg else throw noMessageError()
}

@Suppress("NOTHING_TO_INLINE")
private inline fun noMessageError() =
  IllegalStateException("called getMessage() on an UninstallResponse that has no message")
