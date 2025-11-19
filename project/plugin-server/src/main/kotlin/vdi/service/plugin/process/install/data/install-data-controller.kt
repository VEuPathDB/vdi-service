package vdi.service.plugin.process.install.data

import io.ktor.server.application.ApplicationCall
import vdi.io.plugin.responses.PluginResponseStatus
import vdi.io.plugin.responses.ServerErrorResponse
import vdi.service.plugin.model.ApplicationContext
import vdi.service.plugin.model.MissingDependencyError
import vdi.service.plugin.server.respondJSON

suspend fun ApplicationCall.handleInstallDataRequest(appCtx: ApplicationContext) {
  withInstallDataContext(appCtx) { installCtx ->
    try {
      respondJSON(
        InstallDataHandler(installCtx, appCtx.executor, appCtx.metrics.scriptMetrics)
          .run(),
        PluginResponseStatus.Success,
      )
    } catch (e: MissingDependencyError) {
      respondJSON(e.toResponse(), PluginResponseStatus.MissingDependencyError)
    } catch (e: InstallDataHandler.InstallDirConflictError) {
      respondJSON(ServerErrorResponse(e.message!!), PluginResponseStatus.ServerError)
    }
  }
}
