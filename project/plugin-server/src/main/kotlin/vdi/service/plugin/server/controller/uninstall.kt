package vdi.service.plugin.server.controller

import io.ktor.server.application.*
import vdi.service.plugin.model.ApplicationContext
import vdi.service.plugin.server.context.withUninstallContext
import vdi.service.plugin.server.respond204
import vdi.service.plugin.service.UninstallDataHandler

suspend fun ApplicationCall.handleUninstallRequest(appCtx: ApplicationContext) {
  withUninstallContext { workspace, request ->
      UninstallDataHandler(
        workspace   = workspace,
        request     = request,
        dbDetails   = dbDetails,
        executor    = appCtx.executor,
        customPath  = appCtx.config.customPath,
        installPath = appCtx.pathFactory.makePath(request.installTarget, request.vdiID),
        script      = appCtx.config.uninstallScript,
        metrics     = appCtx.metrics.scriptMetrics,
      )
        .run()
      respond204()
  }
}
