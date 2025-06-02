package vdi.core.plugin.client.response.imp

sealed interface ImportValidationErrorResponse : ImportResponse {
  override val type: ImportResponseType
    get() = ImportResponseType.ValidationError

  val warnings: Collection<String>
}
