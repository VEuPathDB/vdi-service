package vdi.component.plugin.client.response.imp

sealed interface ImportUnhandledErrorResponse : ImportResponse {
  override val type: ImportResponseType
    get() = ImportResponseType.UnhandledError

  val message: String
}
