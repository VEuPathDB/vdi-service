package vdi.service.plugin.server.errors

import io.ktor.server.routing.RoutingContext
import org.slf4j.LoggerFactory
import vdi.io.plugin.responses.PluginResponseStatus
import vdi.io.plugin.responses.ServerErrorResponse
import vdi.service.plugin.model.ExpectedError
import vdi.service.plugin.server.respondJSON

private val log = LoggerFactory.getLogger("ExceptionMiddleware")

suspend fun RoutingContext.withExceptionMapping(
  fn: suspend RoutingContext.() -> Unit
) {
  try {
    fn()
  } catch (e: Throwable) {
    if (e is ExpectedError) {
      call.respondJSON(e.toResponse(), e.status)
    } else {
      log.error("Uncaught exception", e)
      call.respondJSON(ServerErrorResponse(e.message ?: "(null exception message)"), PluginResponseStatus.ServerError)
    }
  }
}
