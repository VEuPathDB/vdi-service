package vdi.service.plugin.server.context

import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.ContentType
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.UnsupportedMediaTypeException
import io.ktor.server.request.contentType
import io.ktor.server.request.receiveStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import vdi.json.JSON
import vdi.model.api.internal.UninstallRequest
import vdi.service.plugin.model.ApplicationContext
import vdi.util.fs.TempFiles

suspend fun ApplicationCall.withUninstallContext(
  appCtx: ApplicationContext,
  fn: suspend (UninstallDataContext) -> Unit,
) {
  if (!request.contentType().match(ContentType.Application.Json))
    throw UnsupportedMediaTypeException(request.contentType())

  val body = parseBody()

  TempFiles.withTempDirectory { workspace -> withDatabaseDetails(body.installTarget, body.type) {
    fn(UninstallDataContext(
      workspace      = workspace,
      customPath     = appCtx.config.customPath,
      request        = body,
      installPath    = appCtx.pathFactory.makePath(body.installTarget, body.vdiID),
      databaseConfig = it,
      scriptConfig   = appCtx.config.uninstallScript,
    ))
  } }
}

private suspend fun ApplicationCall.parseBody(): UninstallRequest {
  val out = withContext(Dispatchers.IO) { receiveStream().use { JSON.readValue<UninstallRequest>(it) } }

  if (out.installTarget.isBlank())
    throw BadRequestException("projectID must not be blank")

  return out
}
