package vdi.lib.plugin.client.response.ind

sealed interface InstallDataValidationFailureResponse : InstallDataResponse {
  override val type: InstallDataResponseType
    get() = InstallDataResponseType.ValidationFailure

  val warnings: Collection<String>
}
