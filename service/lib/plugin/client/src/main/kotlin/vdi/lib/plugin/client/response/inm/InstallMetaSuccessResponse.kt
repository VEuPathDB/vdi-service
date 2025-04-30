package vdi.lib.plugin.client.response.inm

sealed interface InstallMetaSuccessResponse : InstallMetaResponse {
  override val type: InstallMetaResponseType
    get() = InstallMetaResponseType.Success
}
