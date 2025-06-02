package vdi.service.plugin.server.errors

import io.ktor.http.*
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.UnsupportedMediaTypeException
import io.ktor.server.response.*
import io.ktor.util.pipeline.PipelineContext
import org.slf4j.LoggerFactory
import vdi.json.toJSONString
import vdi.model.api.internal.SimpleErrorResponse

private val log = LoggerFactory.getLogger("ExceptionMiddleware")

suspend fun PipelineContext<*, ApplicationCall>.withExceptionMapping(
  fn: suspend PipelineContext<*, ApplicationCall>.() -> Unit
) {
  try {
    fn()
  } catch (e: Throwable) {
    when (e) {
      is BadRequestException -> {
        log.debug("Thrown 400 exception.", e)
        call.respondText(
          SimpleErrorResponse(e.message ?: "null").toJSONString(),
          ContentType.Application.Json,
          HttpStatusCode.BadRequest,
        )
      }

      is UnsupportedMediaTypeException -> {
        log.debug("Thrown 415 exception.", e)
        call.respondText(
          SimpleErrorResponse(e.message ?: "null").toJSONString(),
          ContentType.Application.Json,
          HttpStatusCode.UnsupportedMediaType,
        )
      }

      else                             -> {
        log.error("Uncaught exception", e)
        call.respondText(
          SimpleErrorResponse(e.message ?: "null").toJSONString(),
          ContentType.Application.Json,
          HttpStatusCode.InternalServerError,
        )
      }
    }
  }
}
