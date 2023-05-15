package org.veupathdb.vdi.lib.handler.client.response.imp

import java.io.InputStream

internal data class ImportSuccessResponseImpl(override val resultArchive: InputStream) : ImportSuccessResponse {
  override fun close() {
    resultArchive.close()
  }
}