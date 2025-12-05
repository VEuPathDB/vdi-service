package vdi.service.plugin.process.install.meta

import io.ktor.server.application.ApplicationCall
import vdi.io.plugin.responses.PluginResponseStatus
import vdi.io.plugin.responses.ScriptErrorResponse
import vdi.io.plugin.responses.ServerErrorResponse
import vdi.io.plugin.responses.ValidationResponse
import vdi.service.plugin.model.ApplicationContext
import vdi.service.plugin.server.respond204
import vdi.service.plugin.server.respondJSON

internal suspend fun ApplicationCall.handleInstallMetaRequest(appCtx: ApplicationContext) {
  withInstallMetaContext(appCtx) {
    InstallMetaHandler.run(appCtx.executor, appCtx.metrics.scriptMetrics)
      .also { when (it) {
        is ValidationResponse  -> respondJSON(it, PluginResponseStatus.ValidationError)
        is ScriptErrorResponse -> respondJSON(it, PluginResponseStatus.ScriptError)
        is ServerErrorResponse -> respondJSON(it, PluginResponseStatus.ServerError)
        null -> respond204()
      } }
  }
}
