package vdi.service.plugin.process.preprocess

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respondOutputStream
import org.slf4j.LoggerFactory
import kotlin.io.path.inputStream
import vdi.service.plugin.model.ApplicationContext

private val log = LoggerFactory.getLogger("import-controller")

suspend fun ApplicationCall.handleImportRequest(appCtx: ApplicationContext) {
  withImportContext(appCtx) { importCtx ->
    val outFile = ImportHandler(importCtx, appCtx.executor, appCtx.metrics.scriptMetrics)
      .run()

    log.debug("sending import response body for dataset {}", importCtx.request.vdiID)
    respondOutputStream(ContentType.Application.OctetStream, HttpStatusCode.OK) {
      outFile.inputStream().use { it.transferTo(this) }
    }
  }
}
