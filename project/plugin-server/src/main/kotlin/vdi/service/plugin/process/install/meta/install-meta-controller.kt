package vdi.service.plugin.process.install.meta

import io.ktor.server.application.ApplicationCall
import vdi.service.plugin.model.ApplicationContext
import vdi.service.plugin.server.respond204

suspend fun ApplicationCall.handleInstallMetaRequest(appCtx: ApplicationContext) {
  withInstallMetaContext(appCtx) { context ->
    InstallMetaHandler(context, appCtx.executor, appCtx.metrics.scriptMetrics)
      .run()

    respond204()
  }
}
