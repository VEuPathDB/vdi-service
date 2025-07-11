package vdi.service.plugin.server.controller

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respondOutputStream
import org.slf4j.LoggerFactory
import kotlin.io.path.inputStream
import vdi.model.api.internal.SimpleErrorResponse
import vdi.model.api.internal.WarningResponse
import vdi.service.plugin.model.ApplicationContext
import vdi.service.plugin.server.context.withImportContext
import vdi.service.plugin.server.respondJSON400
import vdi.service.plugin.server.respondJSON418
import vdi.service.plugin.service.ImportHandler

private val log = LoggerFactory.getLogger("import-controller")

suspend fun ApplicationCall.handleImportRequest(appCtx: ApplicationContext) {
  withImportContext(appCtx) { importCtx ->
    try {
      val outFile = ImportHandler(importCtx, appCtx.executor, appCtx.metrics.scriptMetrics)
        .run()

      log.debug("sending import response body for dataset {}", importCtx.request.vdiID)
      respondOutputStream(ContentType.Application.OctetStream, HttpStatusCode.OK) {
        outFile.inputStream().use { it.transferTo(this) }
      }
    } catch (e: ImportHandler.ValidationError) {
      respondJSON418(WarningResponse(e.warnings))
    } catch (e: ImportHandler.EmptyInputError) {
      respondJSON400(SimpleErrorResponse(e.message!!))
    }
  }
}
