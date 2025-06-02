package vdi.service.plugin.server.controller

import io.ktor.server.application.ApplicationCall
import vdi.service.plugin.model.ApplicationContext
import vdi.service.plugin.server.context.withUninstallContext
import vdi.service.plugin.server.respond204
import vdi.service.plugin.service.UninstallDataHandler

suspend fun ApplicationCall.handleUninstallRequest(appCtx: ApplicationContext) {
  withUninstallContext(appCtx) { context ->
      UninstallDataHandler(context, appCtx.executor, appCtx.metrics.scriptMetrics)
        .run()
      respond204()
  }
}
