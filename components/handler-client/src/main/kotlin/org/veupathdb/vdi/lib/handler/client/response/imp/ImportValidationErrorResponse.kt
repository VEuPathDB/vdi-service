package org.veupathdb.vdi.lib.handler.client.response.imp

sealed interface ImportValidationErrorResponse : ImportResponse {
  override val type: ImportResponseType
    get() = ImportResponseType.ValidationError

  val warnings: List<String>
}
