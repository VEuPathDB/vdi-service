package vdi.service.plugin.process.install.data

import io.ktor.server.application.ApplicationCall
import vdi.io.plugin.responses.*
import vdi.service.plugin.model.ApplicationContext
import vdi.service.plugin.server.respondJSON

internal suspend fun ApplicationCall.handleInstallDataRequest(appCtx: ApplicationContext) {
  withInstallDataContext(appCtx) {
    val res = InstallDataHandler.run(appCtx.executor, appCtx.metrics.scriptMetrics)

    respondJSON(res, when (res) {
      is ValidationResponse ->
        if (res.isValid) PluginResponseStatus.Success
        else PluginResponseStatus.ValidationError

      is MissingDependencyResponse -> PluginResponseStatus.MissingDependencyError
      is ScriptErrorResponse       -> PluginResponseStatus.ScriptError
      is ServerErrorResponse       -> PluginResponseStatus.ServerError
    })
  }
}
