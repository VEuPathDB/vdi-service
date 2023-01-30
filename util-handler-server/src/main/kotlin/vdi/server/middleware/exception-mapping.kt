package vdi.server.middleware

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.slf4j.LoggerFactory
import vdi.server.model.make400JSONString
import vdi.server.model.make404JSONString

private val log = LoggerFactory.getLogger("ExceptionMiddleware")

suspend fun PipelineContext<*, ApplicationCall>.withExceptionMapping(
  fn: suspend PipelineContext<*, ApplicationCall>.() -> Unit
) {
  try {
    fn()
  } catch (e: Throwable) {
    when (e) {
      is HTTPError400 -> {
        log.debug("Thrown 400 exception: ${e.message}", e)
        call.respond(HttpStatusCode.BadRequest, make400JSONString(e.message!!))
      }

      is HTTPError404 -> {
        log.trace("Thrown 404 exception.")
        call.respond(HttpStatusCode.NotFound, make404JSONString(e.message!!))
      }

      else -> {
        log.error("Uncaught exception", e)
        call.respond(HttpStatusCode.InternalServerError, e.message!!)
      }
    }
  }
}

class HTTPError404() : RuntimeException("resource not found")
class HTTPError400(message: String) : RuntimeException(message)