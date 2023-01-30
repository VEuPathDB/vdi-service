package vdi.server

import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import vdi.server.controller.handleImportProcessingPost

fun Application.configureRouting() {
  routing {
    configureMetricsRoute(this@configureRouting)

    get("/{name}") {
      call.request
      call.respondText("goodbye ${call.parameters["name"]}")
    }

    post("/import-processing/{vdi-id}") {
      handleImportProcessingPost()
    }
  }
}

private fun Routing.configureMetricsRoute(app: Application) {
  val micrometer = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

  app.install(MicrometerMetrics) { registry = micrometer }
  get("/metrics") {
    call.respond(micrometer.scrape())
  }
}

