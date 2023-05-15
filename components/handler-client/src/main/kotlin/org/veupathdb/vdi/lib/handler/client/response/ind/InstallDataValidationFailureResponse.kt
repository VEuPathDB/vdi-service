package org.veupathdb.vdi.lib.handler.client.response.ind

sealed interface InstallDataValidationFailureResponse : InstallDataResponse {
  override val type: InstallDataResponseType
    get() = InstallDataResponseType.ValidationFailure

  val warnings: List<String>
}
