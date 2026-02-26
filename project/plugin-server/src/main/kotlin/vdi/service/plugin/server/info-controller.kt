package vdi.service.plugin.server

import io.ktor.http.ContentType
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respondText
import vdi.io.plugin.responses.ServerInfoResponse
import vdi.json.JSON
import vdi.service.plugin.model.ApplicationContext

internal suspend fun ApplicationCall.handleServiceInfoRequest(appCtx: ApplicationContext) {
  respondText(JSON.writeValueAsString(ServerInfoResponse(appCtx.config.manifest)), ContentType.Application.Json)
}