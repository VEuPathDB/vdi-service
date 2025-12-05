package vdi.service.plugin.process.preprocess

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.http.content.LocalPathContent
import io.ktor.server.response.respond
import vdi.io.plugin.responses.PluginResponseStatus
import vdi.io.plugin.responses.ScriptErrorResponse
import vdi.io.plugin.responses.ServerErrorResponse
import vdi.io.plugin.responses.ValidationResponse
import vdi.service.plugin.model.ApplicationContext
import vdi.service.plugin.server.respondJSON
import vdi.util.fn.fold
import vdi.util.fn.mapLeft

internal suspend fun ApplicationCall.handleImportRequest(appCtx: ApplicationContext) {
  withImportContext(appCtx) {
    ImportHandler.run(appCtx.executor, appCtx.metrics.scriptMetrics)
      .mapLeft { LocalPathContent(it, ContentType.Application.Zip) }
      .fold(
        {
          logger.info("sending import success response")
          respond(HttpStatusCode.OK, it) },
        { when (it) {
          is ValidationResponse -> {
            logger.info("sending import validation failure response")
            respondJSON(it, PluginResponseStatus.ValidationError)
          }
          is ScriptErrorResponse -> respondJSON(it, PluginResponseStatus.ScriptError)
          is ServerErrorResponse -> respondJSON(it, PluginResponseStatus.ServerError)
        } }
      )
  }
}
