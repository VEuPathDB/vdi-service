package vdi.service.plugin.server.errors

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.RoutingContext
import org.slf4j.LoggerFactory
import vdi.io.plugin.responses.ServerErrorResponse
import vdi.json.toJSONString

private val log = LoggerFactory.getLogger("ExceptionMiddleware")

suspend fun RoutingContext.withExceptionMapping(fn: suspend RoutingContext.() -> Unit) {
  try {
    fn()
  } catch (e: Throwable) {
    log.error("Uncaught exception", e)
    call.respondText(
      ServerErrorResponse(e.message ?: "(null exception message)").toJSONString(),
      ContentType.Application.Json,
      HttpStatusCode.InternalServerError,
    )
  }
}
