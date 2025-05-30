package vdi.lib.plugin.client.response.ind

sealed interface InstallDataBadRequestResponse : InstallDataResponse {
  override val type: InstallDataResponseType
    get() = InstallDataResponseType.BadRequest

  val message: String
}
