package vdi.component.plugin.client.response.ind

sealed interface InstallDataUnexpectedErrorResponse : InstallDataResponse {
  override val type: InstallDataResponseType
    get() = InstallDataResponseType.UnexpectedError

  val message: String
}

