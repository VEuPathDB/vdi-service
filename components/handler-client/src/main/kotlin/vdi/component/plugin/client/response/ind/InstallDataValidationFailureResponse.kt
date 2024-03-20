package vdi.component.plugin.client.response.ind

sealed interface InstallDataValidationFailureResponse : InstallDataResponse {
  override val type: InstallDataResponseType
    get() = InstallDataResponseType.ValidationFailure

  val warnings: List<String>
}
