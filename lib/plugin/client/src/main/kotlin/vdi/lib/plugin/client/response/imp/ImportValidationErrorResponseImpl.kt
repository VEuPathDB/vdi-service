package vdi.lib.plugin.client.response.imp

internal data class ImportValidationErrorResponseImpl(override val warnings: Collection<String>)
  : ImportValidationErrorResponse
