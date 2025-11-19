package vdi.core.plugin.client.response

data object EmptySuccessResponse: SuccessResponse, InstallMetaResponse, UninstallResponse {
  override val body: Unit
    get() = Unit
}