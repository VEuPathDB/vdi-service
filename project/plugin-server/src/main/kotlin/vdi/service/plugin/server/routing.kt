package vdi.service.plugin.server

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import vdi.model.api.internal.Endpoint
import vdi.service.plugin.model.ApplicationContext
import vdi.service.plugin.server.controller.handleImportRequest
import vdi.service.plugin.server.controller.handleInstallDataRequest
import vdi.service.plugin.server.controller.handleInstallMetaRequest
import vdi.service.plugin.server.controller.handleUninstallRequest
import vdi.service.plugin.server.errors.withExceptionMapping

fun Application.configureServer(appCtx: ApplicationContext) {

  install(MicrometerMetrics) { registry = appCtx.metrics.micrometer }

  routing {
    post(Endpoint.Import) { withExceptionMapping { call.handleImportRequest(appCtx) } }

    route(Endpoint.InstallPathRoot) {
      post(Endpoint.DataSubPath) { withExceptionMapping { call.handleInstallDataRequest(appCtx) } }
      post(Endpoint.MetaSubPath) { withExceptionMapping { call.handleInstallMetaRequest(appCtx) } }
    }

    post(Endpoint.Uninstall) { withExceptionMapping { call.handleUninstallRequest(appCtx) } }

    get("/metrics") { call.respond(appCtx.metrics.micrometer.scrape()) }
  }
}
