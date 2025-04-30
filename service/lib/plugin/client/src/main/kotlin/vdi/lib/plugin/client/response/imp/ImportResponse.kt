package vdi.lib.plugin.client.response.imp

import vdi.lib.plugin.client.PluginHandlerClientResponse

/**
 * Import Client Response
 *
 * A [PluginHandlerClientResponse] that must be one of the following types:
 *
 * * [ImportSuccessResponse]
 * * [ImportBadRequestResponse]
 * * [ImportValidationErrorResponse]
 * * [ImportUnhandledErrorResponse]
 */
sealed interface ImportResponse : PluginHandlerClientResponse {

  override val isSuccessResponse: Boolean
    get() = type == ImportResponseType.Success

  override val responseCode: Int
    get() = type.code

  /**
   * The response type for this API response object.
   *
   * The type indicates the HTTP response code and maps to one of the 4 possible
   * implementing subtypes of this interface:
   *
   * * [ImportSuccessResponse]
   * * [ImportBadRequestResponse]
   * * [ImportValidationErrorResponse]
   * * [ImportUnhandledErrorResponse]
   */
  val type: ImportResponseType
}

