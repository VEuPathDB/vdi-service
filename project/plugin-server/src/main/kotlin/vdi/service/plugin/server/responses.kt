package vdi.service.plugin.server

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respondText
import vdi.json.toJSONString

suspend inline fun ApplicationCall.respond204() =
  respondText("", ContentType.Text.Plain, StatusNoContent)

suspend inline fun ApplicationCall.respondJSON200(body: Any) =
  respondJSON(body, StatusSuccess)

suspend inline fun ApplicationCall.respondJSON400(body: Any) =
  respondJSON(body, StatusBadRequest)

suspend inline fun ApplicationCall.respondJSON418(body: Any) =
  respondJSON(body, StatusValidationError)

suspend inline fun ApplicationCall.respondJSON420(body: Any) =
  respondJSON(body, StatusCompatibilityError)

suspend inline fun ApplicationCall.respondJSON500(body: Any) =
  respondJSON(body, StatusServerError)

suspend inline fun ApplicationCall.respondJSON(body: Any, status: HttpStatusCode) =
  respondText(body.toJSONString(), ContentType.Application.Json, status)