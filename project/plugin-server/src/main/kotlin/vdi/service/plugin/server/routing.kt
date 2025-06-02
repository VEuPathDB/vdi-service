package vdi.service.plugin.server

import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import vdi.model.api.internal.Endpoint
import vdi.service.plugin.server.errors.withExceptionMapping
import vdi.service.plugin.model.ApplicationContext
import vdi.service.plugin.server.controller.*

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
