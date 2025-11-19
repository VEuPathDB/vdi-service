package vdi.service.plugin.server.errors

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.util.pipeline.PipelineContext
import org.slf4j.LoggerFactory
import vdi.io.plugin.responses.PluginResponseStatus
import vdi.io.plugin.responses.ServerErrorResponse
import vdi.service.plugin.model.ExpectedError
import vdi.service.plugin.server.respondJSON

private val log = LoggerFactory.getLogger("ExceptionMiddleware")

suspend fun PipelineContext<*, ApplicationCall>.withExceptionMapping(
  fn: suspend PipelineContext<*, ApplicationCall>.() -> Unit
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
