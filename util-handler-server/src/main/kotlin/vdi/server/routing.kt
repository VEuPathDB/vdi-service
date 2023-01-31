package vdi.server

import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import vdi.server.controller.handlePostImport
import vdi.server.controller.handlePostInstallData
import vdi.server.controller.handlePostInstallMeta
import vdi.server.controller.handlePostUninstall
import vdi.server.middleware.withExceptionMapping


fun Application.configureRouting() {
  val micrometer = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

  install(MicrometerMetrics) { registry = micrometer }

  routing {
    post("/import") {
      withExceptionMapping {
        handlePostImport()
      }
    }

    route("/install") {
      post("/meta") {
        withExceptionMapping {
          handlePostInstallMeta()
        }
      }

      post("/data") {
        withExceptionMapping {
          handlePostInstallData()
        }
      }
    }

    post("/uninstall") {
      withExceptionMapping {
        handlePostUninstall()
      }
    }

    get("/metrics") {
      call.respond(micrometer.scrape())
    }
  }
}
