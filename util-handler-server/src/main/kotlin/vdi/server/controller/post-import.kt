package vdi.server.controller

import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import vdi.server.middleware.HTTPError400
import vdi.service.importing.processImport
import vdi.util.requireValidVDIID
import vdi.util.withTempDirectory

suspend fun PipelineContext<*, ApplicationCall>.handlePostImport() {
  val (vdiID, partData) = call.parseBody()

  withTempDirectory { tempDir ->
    val archive = tempDir.resolve("$vdiID.tar.gz")

    withContext(Dispatchers.IO) {
      archive.createNewFile()
      archive.outputStream().use { partData.streamProvider().transferTo(it) }
    }

    partData.dispose()

    call.processImport(vdiID, tempDir, archive)
  }
}

/**
 * Parse Multipart Body
 *
 * Reads through the multipart entries and returns the two that are relevant,
 * failing if any unexpected entries are encountered.
 */
suspend fun ApplicationCall.parseBody() : Pair<String, PartData.FileItem> {
  var vdiID: String? = null
  var file:  PartData.FileItem? = null

  receiveMultipart().forEachPart {
    when (it) {
      is PartData.FormItem -> {
        if (it.name != "vdi-id")
          throw HTTPError400("unexpected form item in form data")

        if (vdiID != null)
          throw HTTPError400("vdi-id form item specified more than once")

        vdiID = requireValidVDIID(it.value)
          ?: throw HTTPError400("invalid vdi-id format")

        it.dispose()
      }

      is PartData.FileItem -> {
        if (it.name != "payload")
          throw HTTPError400("unexpected file item in form data")

        if (file != null)
          throw HTTPError400("payload form item specified more than once")

        file = it
      }

      else -> {
        throw HTTPError400("unexpected untyped item in form data")
      }
    }
  }

  if (vdiID == null)
    throw HTTPError400("vdi-id form item was missing")
  if (file == null)
    throw HTTPError400("payload form item was missing")

  return Pair(vdiID!!, file!!)
}

// TODO: errors that are recoverable vs errors that are not recoverable...
//       response codes?  this is an RPC api, so maybe look at what the standard
//       approach is for this?   Why roll our own when gRPC exists?