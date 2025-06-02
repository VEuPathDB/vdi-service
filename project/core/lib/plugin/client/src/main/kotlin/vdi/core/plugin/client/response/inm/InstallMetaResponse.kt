package vdi.core.plugin.client.response.inm

import vdi.core.plugin.client.PluginHandlerClientResponse

/**
 * Install Meta Client Response
 *
 * A [PluginHandlerClientResponse] that must be one of the following types:
 *
 * * [InstallMetaSuccessResponse]
 * * [InstallMetaBadRequestResponse]
 * * [InstallMetaUnexpectedErrorResponse]
 */
sealed interface InstallMetaResponse : PluginHandlerClientResponse {

  override val isSuccessResponse: Boolean
    get() = type == InstallMetaResponseType.Success

  override val responseCode: Int
    get() = type.code

  /**
   * The response type for this API response object.
   *
   * The type indicates the HTTP response code and maps to one of the 3 possible
   * implementing subtypes of this interface:
   *
   * * [InstallMetaSuccessResponse]
   * * [InstallMetaBadRequestResponse]
   * * [InstallMetaUnexpectedErrorResponse]
   */
  val type: InstallMetaResponseType
}
