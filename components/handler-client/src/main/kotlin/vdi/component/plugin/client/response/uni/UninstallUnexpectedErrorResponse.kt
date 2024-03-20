package vdi.component.plugin.client.response.uni

sealed interface UninstallUnexpectedErrorResponse : UninstallResponse {
  override val type: UninstallResponseType
    get() = UninstallResponseType.UnexpectedError

  val message: String
}
