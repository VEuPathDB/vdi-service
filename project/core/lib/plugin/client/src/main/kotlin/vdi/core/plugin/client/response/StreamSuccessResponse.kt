package vdi.core.plugin.client.response

import java.io.InputStream
import vdi.util.zip.zipEntries

data class StreamSuccessResponse(override val body: InputStream): SuccessResponse, ImportResponse {
  fun asZip() =
    body.zipEntries()
}