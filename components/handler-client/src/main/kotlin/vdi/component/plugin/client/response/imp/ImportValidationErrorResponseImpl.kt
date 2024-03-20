package vdi.component.plugin.client.response.imp

internal data class ImportValidationErrorResponseImpl(override val warnings: List<String>)
  : ImportValidationErrorResponse