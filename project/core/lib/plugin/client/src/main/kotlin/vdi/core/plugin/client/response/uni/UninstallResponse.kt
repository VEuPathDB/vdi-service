package vdi.core.plugin.client.response.uni

import vdi.core.plugin.client.PluginHandlerClientResponse

/**
 * Uninstall Client Response
 */
sealed interface UninstallResponse: PluginHandlerClientResponse {
  override val isSuccessResponse: Boolean
    get() = type == UninstallResponseType.Success

  override val responseCode: Int
    get() = type.code

  /**
   * The response type for this API response object.
   *
   * The type indicates the HTTP response code and maps to one of the 3 possible
   * implementing subtypes of this interface:
   */
  val type: UninstallResponseType
}
