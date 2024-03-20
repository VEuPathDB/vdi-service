package vdi.component.plugin.client.response.ind

import vdi.component.plugin.client.PluginHandlerClientResponse

/**
 * Install Data Client Response
 *
 * A [PluginHandlerClientResponse] that must be one of the following types:
 *
 * * [InstallDataSuccessResponse]
 * * [InstallDataBadRequestResponse]
 * * [InstallDataValidationFailureResponse]
 * * [InstallDataMissingDependenciesResponse]
 * * [InstallDataUnexpectedErrorResponse]
 */
sealed interface InstallDataResponse : PluginHandlerClientResponse {
  override val isSuccessResponse: Boolean
    get() = type == InstallDataResponseType.Success

  override val responseCode: Int
    get() = type.code

  /**
   * The response type for this API response object.
   *
   * The type indicates the HTTP response code and maps to one of the 5 possible
   * implementing subtypes of this interface:
   *
   * * [InstallDataSuccessResponse]
   * * [InstallDataBadRequestResponse]
   * * [InstallDataValidationFailureResponse]
   * * [InstallDataMissingDependenciesResponse]
   * * [InstallDataUnexpectedErrorResponse]
   */
  val type: InstallDataResponseType
}
