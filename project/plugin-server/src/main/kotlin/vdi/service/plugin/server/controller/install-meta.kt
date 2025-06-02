package vdi.service.plugin.server.controller

import io.ktor.server.application.ApplicationCall
import vdi.service.plugin.model.ApplicationContext
import vdi.service.plugin.server.context.withInstallMetaContext
import vdi.service.plugin.server.respond204
import vdi.service.plugin.service.InstallMetaHandler

suspend fun ApplicationCall.handleInstallMetaRequest(appCtx: ApplicationContext) {
  withInstallMetaContext(appCtx) { context ->
    InstallMetaHandler(context, appCtx.executor, appCtx.metrics.scriptMetrics)
      .run()

    respond204()
  }
}
