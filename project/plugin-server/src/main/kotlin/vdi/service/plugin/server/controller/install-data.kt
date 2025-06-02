package vdi.service.plugin.server.controller

import io.ktor.server.application.ApplicationCall
import vdi.service.plugin.model.ApplicationContext
import vdi.model.api.internal.SimpleErrorResponse
import vdi.model.api.internal.WarningResponse
import vdi.service.plugin.server.context.withDatabaseDetails
import vdi.service.plugin.server.context.withInstallDataContext
import vdi.service.plugin.server.respondJSON200
import vdi.service.plugin.server.respondJSON418
import vdi.service.plugin.server.respondJSON420
import vdi.service.plugin.server.respondJSON500
import vdi.service.plugin.service.InstallDataHandler

suspend fun ApplicationCall.handleInstallDataRequest(appCtx: ApplicationContext) {
  withInstallDataContext { installCtx ->
    withDatabaseDetails(
      installCtx.request.installTarget,
      installCtx.meta.type
    ) { dbDetails ->
      try {
        // Run the install-data service and collect the returned list of
        // installation warnings.
        val warnings = InstallDataHandler(
          context         = installCtx,
          dbDetails          = dbDetails,
          executor           = appCtx.executor,
          customPath         = appCtx.config.customPath,
          datasetInstallPath = appCtx.pathFactory.makePath(installCtx.request.installTarget, installCtx.request.vdiID),
          metaScript         = appCtx.config.installMetaScript,
          dataScript         = appCtx.config.installDataScript,
          compatScript       = appCtx.config.checkCompatScript,
          metrics            = appCtx.metrics.scriptMetrics,
        )
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
}
