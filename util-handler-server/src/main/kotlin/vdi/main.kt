package vdi

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.netty.*
import vdi.conf.Configuration
import vdi.server.configureRouting

fun main() {

  embeddedServer(
    Netty,
    port = 8080, // Configuration.serverConfiguration.port.toInt(),
    host = Configuration.serverConfiguration.host,
    module = Application::module
  )
    .start(true)
}

fun Application.module() {
  configureRouting()
}
