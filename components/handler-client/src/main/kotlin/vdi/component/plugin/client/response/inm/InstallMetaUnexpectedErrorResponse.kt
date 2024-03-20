package vdi.component.plugin.client.response.inm

sealed interface InstallMetaUnexpectedErrorResponse : InstallMetaResponse {
  override val type: InstallMetaResponseType
    get() = InstallMetaResponseType.UnexpectedError

  val message: String
}
