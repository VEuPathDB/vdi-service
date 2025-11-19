package vdi.service.plugin.process.uninstall

import io.ktor.server.application.ApplicationCall
import vdi.service.plugin.model.ApplicationContext
import vdi.service.plugin.server.respond204

suspend fun ApplicationCall.handleUninstallRequest(appCtx: ApplicationContext) {
  withUninstallContext(appCtx) { context ->
    UninstallDataHandler(context, appCtx.executor, appCtx.metrics.scriptMetrics).run()
    respond204()
  }
}
