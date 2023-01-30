package vdi

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import vdi.conf.Configuration.ServerConfiguration
import vdi.server.configureRouting

fun main() {

  embeddedServer(
    Netty,
    port = ServerConfiguration.port.toInt(),
    host = ServerConfiguration.host,
    module = Application::module
  )
    .start(true)
}

fun Application.module() {
  configureRouting()
}
