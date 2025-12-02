package vdi.service.plugin.server

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import vdi.io.plugin.PluginEndpoint
import vdi.service.plugin.model.ApplicationContext
import vdi.service.plugin.process.preprocess.handleImportRequest
import vdi.service.plugin.process.install.data.handleInstallDataRequest
import vdi.service.plugin.process.install.meta.handleInstallMetaRequest
import vdi.service.plugin.process.uninstall.handleUninstallRequest
import vdi.service.plugin.server.errors.withExceptionMapping

fun Application.configureServer(appCtx: ApplicationContext) {

  install(MicrometerMetrics) { registry = appCtx.metrics.micrometer }

  routing {
    post(PluginEndpoint.Import) {
      withExceptionMapping { call.handleImportRequest(appCtx) } }

    route(PluginEndpoint.InstallPathRoot) {
      post(PluginEndpoint.DataSubPath) { withExceptionMapping { call.handleInstallDataRequest(appCtx) } }
      post(PluginEndpoint.MetaSubPath) { withExceptionMapping { call.handleInstallMetaRequest(appCtx) } }
    }

    post(PluginEndpoint.Uninstall) { withExceptionMapping { call.handleUninstallRequest(appCtx) } }

    get("/metrics") { call.respond(appCtx.metrics.micrometer.scrape()) }
  }
}
