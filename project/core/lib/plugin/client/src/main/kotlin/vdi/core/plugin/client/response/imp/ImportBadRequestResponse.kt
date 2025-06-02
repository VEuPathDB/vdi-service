package vdi.core.plugin.client.response.imp

/**
 * "Bad Request" Import Response
 */
sealed interface ImportBadRequestResponse : ImportResponse {
  override val type: ImportResponseType
    get() = ImportResponseType.BadRequest

  val message: String
}
