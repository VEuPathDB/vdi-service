package vdi.core.plugin.client.response.inm

sealed interface InstallMetaSuccessResponse : InstallMetaResponse {
  override val type: InstallMetaResponseType
    get() = InstallMetaResponseType.Success
}
