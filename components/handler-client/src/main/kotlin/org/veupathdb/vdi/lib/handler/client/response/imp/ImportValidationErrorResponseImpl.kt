package org.veupathdb.vdi.lib.handler.client.response.imp

internal data class ImportValidationErrorResponseImpl(override val warnings: List<String>)
  : ImportValidationErrorResponse