package vdi.server.controller

import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import vdi.server.middleware.HTTPError400
import vdi.server.middleware.HTTPError404
import vdi.service.importing.processImport
import vdi.util.requireValidVDIID
import vdi.util.withTempDirectory

suspend fun PipelineContext<*, ApplicationCall>.handleImportProcessingPost() {
  val vdiID = call.parameters["vdi-id"]?.let(::requireValidVDIID) ?: throw HTTPError404()

  val partData = call.receiveMultipart()
    .readPart()
    .let {
      if (it == null)
        throw HTTPError400("no multipart body provided")
      it
    }

  if (partData.name != "payload")
    throw HTTPError400("unexpected form param submitted")

  if (partData !is PartData.FileItem)
    throw HTTPError400("expected form param was not of type file")

  withTempDirectory { tempDir ->
    val archive = tempDir.resolve("$vdiID.tar.gz")

    withContext(Dispatchers.IO) {
      // Create the temp file for the archive being posted
      archive.createNewFile()
      // transfer the archive contents to the file
      archive.outputStream().use { partData.streamProvider().transferTo(it) }
    }

    call.processImport(vdiID, tempDir, archive)
  }
}

// TODO: errors that are recoverable vs errors that are not recoverable...
//       response codes?  this is an RPC api, so maybe look at what the standard
//       approach is for this?   Why roll our own when gRPC exists?