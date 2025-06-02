package vdi.lib.plugin.client.response.ind

sealed interface InstallDataSuccessResponse : InstallDataResponse {
  override val type: InstallDataResponseType
    get() = InstallDataResponseType.Success

  val warnings: Collection<String>
}
