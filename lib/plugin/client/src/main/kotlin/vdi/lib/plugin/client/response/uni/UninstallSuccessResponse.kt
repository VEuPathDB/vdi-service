package vdi.lib.plugin.client.response.uni

sealed interface UninstallSuccessResponse : UninstallResponse {
  override val type: UninstallResponseType
    get() = UninstallResponseType.Success
}
