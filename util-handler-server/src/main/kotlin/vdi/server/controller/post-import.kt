package vdi.server.controller

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import vdi.server.model.make400JSONString
import vdi.server.model.make404JSONString
import vdi.util.JSON
import vdi.util.makeTempDirectory
import vdi.util.requireValidVDIID
import java.io.File

suspend fun PipelineContext<*, ApplicationCall>.handleImportProcessingPost() {
  val vdiID = call.parameters["vdi-id"]?.let(::requireValidVDIID)

  if (vdiID == null) {
    call.respond(HttpStatusCode.NotFound, make404JSONString("unrecognized VDI ID"))
    return
  }

  var unexpectedFields = false

  var manifestJSON = ""
  var metaJSON = ""
  var tempFiles = mutableListOf<File>()
  val inputDirectory = makeTempDirectory()

  val multipart = call.receiveMultipart()

  multipart.forEachPart { part ->
    when (part) {
      is PartData.FormItem -> {
        when (part.name) {
          "manifest" -> manifestJSON = part.value
          "meta"     -> metaJSON = part.value
        }
      }

      is PartData.FileItem -> {
        val name = part.originalFileName ?: part.name!!
        val file = inputDirectory.resolve(name)

        withContext(Dispatchers.IO) {
          file.createNewFile()
        }
        file.outputStream()
          .buffered()
          .use { part.streamProvider().transferTo(it) }

        tempFiles.add(file)
      }

      else -> {}
    }
  }

  if (manifestJSON.isBlank()) {
    call.respond(HttpStatusCode.BadRequest, make400JSONString("empty or absent manifest value"))
    inputDirectory.deleteRecursively()
    return
  }

  if (metaJSON.isBlank()) {
    call.respond(HttpStatusCode.BadRequest, make400JSONString("empty or absent meta value"))
    inputDirectory.deleteRecursively()
    return
  }

  if (tempFiles.isEmpty()) {
    call.respond(HttpStatusCode.BadRequest, make400JSONString("no dataset files were provided"))
    inputDirectory.deleteRecursively()
    return
  }

  val manifest = JSON.readValue(manifestJSON, Data)

}