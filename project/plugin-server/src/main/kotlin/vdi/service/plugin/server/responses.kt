package vdi.service.plugin.server

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respondBytesWriter
import io.ktor.server.response.respondText
import io.ktor.utils.io.jvm.nio.asSource
import io.ktor.utils.io.writeSource
import kotlinx.io.buffered
import java.nio.channels.ReadableByteChannel
import vdi.io.plugin.responses.PluginResponseStatus
import vdi.json.toJSONString
import vdi.service.plugin.server.context.ScriptContext
import vdi.service.plugin.util.ReadableByteArrayChannel

context(ctx: ScriptContext<*, *>)
internal suspend inline fun ApplicationCall.respond204() {
  ctx.logger.info("responding with HTTP 204")
  respondText("", ContentType.Text.Plain, HttpStatusCode.NoContent)
}

context(ctx: ScriptContext<*, *>)
internal suspend inline fun ApplicationCall.respondJSON(body: Any, status: PluginResponseStatus) {
  ctx.logger.info("responding with HTTP ${status.code}")
  respondText(body.toJSONString(), ContentType.Application.Json, HttpStatusCode.fromValue(status.code.toInt()))
}

context(ctx: ScriptContext<*, *>)
internal suspend fun ApplicationCall.respondSuccess(
  body:          ReadableByteChannel,
  contentType:   ContentType,
  contentLength: Long = -1,
) {
  ctx.logger.info("responding with HTTP 200")
  respondBytesWriter(contentType, HttpStatusCode.OK, if (contentLength == -1L) null else contentLength) {
    writeSource(body.asSource().buffered())
  }
}

context(ctx: ScriptContext<*, *>)
internal suspend inline fun ApplicationCall.respondSuccess(body: String, contentType: ContentType) {
  body.encodeToByteArray().let { respondSuccess(ReadableByteArrayChannel(it), contentType, it.size.toLong()) }
}

context(ctx: ScriptContext<*, *>)
internal suspend inline fun ApplicationCall.respondSuccessJSON(body: Any) {
  respondSuccess(body.toJSONString(), ContentType.Application.Json)
}
