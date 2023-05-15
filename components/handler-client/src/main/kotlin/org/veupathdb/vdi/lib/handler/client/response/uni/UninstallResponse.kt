package org.veupathdb.vdi.lib.handler.client.response.uni

import org.veupathdb.vdi.lib.handler.client.PluginHandlerClientResponse

/**
 * Uninstall Client Response
 *
 * A [PluginHandlerClientResponse] that must be one of the following types:
 *
 * * [UninstallSuccessResponse]
 * * [UninstallBadRequestResponse]
 * * [UninstallUnexpectedErrorResponse]
 */
sealed interface UninstallResponse : PluginHandlerClientResponse {
  override val isSuccessResponse: Boolean
    get() = type == UninstallResponseType.Success

  override val responseCode: Int
    get() = type.code

  /**
   * The response type for this API response object.
   *
   * The type indicates the HTTP response code and maps to one of the 3 possible
   * implementing subtypes of this interface:
   *
   * * [UninstallSuccessResponse]
   * * [UninstallBadRequestResponse]
   * * [UninstallUnexpectedErrorResponse]
   */
  val type: UninstallResponseType
}
