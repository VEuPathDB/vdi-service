package org.veupathdb.vdi.lib.handler.client.response.imp

sealed interface ImportUnhandledErrorResponse : ImportResponse {
  override val type: ImportResponseType
    get() = ImportResponseType.UnhandledError

  val message: String
}
