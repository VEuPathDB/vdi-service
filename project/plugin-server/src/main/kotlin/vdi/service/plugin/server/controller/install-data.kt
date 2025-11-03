package vdi.service.plugin.server.controller

import io.ktor.server.application.ApplicationCall
import vdi.model.api.internal.SimpleErrorResponse
import vdi.model.api.internal.WarningResponse
import vdi.service.plugin.model.ApplicationContext
import vdi.service.plugin.server.context.withInstallDataContext
import vdi.service.plugin.server.respondJSON200
import vdi.service.plugin.server.respondJSON418
import vdi.service.plugin.server.respondJSON420
import vdi.service.plugin.server.respondJSON500
import vdi.service.plugin.service.InstallDataHandler

suspend fun ApplicationCall.handleInstallDataRequest(appCtx: ApplicationContext) {
  withInstallDataContext(appCtx) { installCtx ->
    try {
      // Run the install-data service and collect the returned list of
      // installation warnings.
      val warnings = InstallDataHandler(installCtx, appCtx.executor, appCtx.metrics.scriptMetrics)
        .run()

      respondJSON200(WarningResponse(warnings))
    } catch (e: InstallDataHandler.ValidationError) {
      respondJSON418(WarningResponse(e.warnings))
    } catch (e: InstallDataHandler.CompatibilityError) {
      respondJSON420(WarningResponse(e.warnings))
    } catch (e: InstallDataHandler.InstallDirConflictError) {
      respondJSON500(SimpleErrorResponse(e.message!!))
    }
  }
}
