package vdi.service.plugin.server.controller

import io.ktor.server.application.*
import vdi.service.plugin.model.ApplicationContext
import vdi.service.plugin.server.context.withDatabaseDetails
import vdi.service.plugin.server.context.withInstallMetaContext
import vdi.service.plugin.server.respond204
import vdi.service.plugin.service.InstallMetaHandler

suspend fun ApplicationCall.handleInstallMetaRequest(appCtx: ApplicationContext) {
  withInstallMetaContext { workspace, request ->
    withDatabaseDetails(request.installTarget, request.meta.type) { dbDetails ->
      InstallMetaHandler(
        workspace   = workspace,
        request     = request,
        dbDetails   = dbDetails,
        executor    = appCtx.executor,
        customPath  = appCtx.config.customPath,
        installPath = appCtx.pathFactory.makePath(request.installTarget, request.vdiID),
        script      = appCtx.config.installMetaScript,
        metrics     = appCtx.metrics.scriptMetrics,
      )
        .run()

      respond204()
    }
  }
}
