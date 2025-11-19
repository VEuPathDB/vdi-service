package vdi.service.plugin.server

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respondText
import vdi.io.plugin.responses.PluginResponseStatus
import vdi.json.toJSONString

suspend inline fun ApplicationCall.respond204() =
  respondText("", ContentType.Text.Plain, HttpStatusCode.NoContent)

suspend inline fun ApplicationCall.respondJSON(body: Any, status: PluginResponseStatus) =
  respondText(body.toJSONString(), ContentType.Application.Json, HttpStatusCode.fromValue(status.code.toInt()))