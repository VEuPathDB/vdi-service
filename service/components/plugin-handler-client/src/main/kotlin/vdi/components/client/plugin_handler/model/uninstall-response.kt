package vdi.components.client.plugin_handler.model


sealed interface UninstallResponse {
  val type: UninstallRequest

  val hasMessage: Boolean

  fun getMessage(): String
}

enum class UninstallResponseType {
  Success,
  BadRequest,
  ServerError,
}

internal object UninstallResponseNoContent : UninstallResponse {
  override val type: UninstallRequest
    get() = TODO("Not yet implemented")
  override val hasMessage: Boolean
    get() = TODO("Not yet implemented")

  override fun getMessage(): String {
    TODO("Not yet implemented")
  }

}