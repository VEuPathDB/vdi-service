package vdi.component.plugin.client.response.imp

import java.io.InputStream

sealed interface ImportSuccessResponse : ImportResponse, AutoCloseable {
  override val type: ImportResponseType
    get() = ImportResponseType.Success

  val resultArchive: InputStream
}
