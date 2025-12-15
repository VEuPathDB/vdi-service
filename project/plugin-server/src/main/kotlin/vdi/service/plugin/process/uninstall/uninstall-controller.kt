package vdi.service.plugin.process.uninstall

import io.ktor.server.application.ApplicationCall
import vdi.io.plugin.responses.PluginResponseStatus
import vdi.io.plugin.responses.ScriptErrorResponse
import vdi.io.plugin.responses.ServerErrorResponse
import vdi.service.plugin.model.ApplicationContext
import vdi.service.plugin.server.respond200
import vdi.service.plugin.server.respondJSON

internal suspend fun ApplicationCall.handleUninstallRequest(appCtx: ApplicationContext) {
  withUninstallContext(appCtx) {
    UninstallDataHandler.run(appCtx.executor, appCtx.metrics.scriptMetrics)
      .also { when (it) {
        is ScriptErrorResponse -> respondJSON(it, PluginResponseStatus.ScriptError)
        is ServerErrorResponse -> respondJSON(it, PluginResponseStatus.ServerError)
        null -> respond200()
      } }
  }
}
