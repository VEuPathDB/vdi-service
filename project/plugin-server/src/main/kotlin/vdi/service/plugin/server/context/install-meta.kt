package vdi.service.plugin.server.context

import com.fasterxml.jackson.core.JacksonException
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.UnsupportedMediaTypeException
import io.ktor.server.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import vdi.model.api.internal.InstallMetaRequest
import vdi.json.JSON
import vdi.util.io.BoundedInputStream
import vdi.service.plugin.model.ApplicationContext
import vdi.util.fs.TempFiles

// Max allowed size of the post body: 32KiB
private const val MAX_INPUT_BYTES = 32768uL

suspend fun ApplicationCall.withInstallMetaContext(
  appCtx: ApplicationContext,
  fn: suspend (InstallMetaContext) -> Unit,
) {
  if (!request.contentType().match(ContentType.Application.Json))
    throw UnsupportedMediaTypeException(request.contentType())

  val body = try {
    withContext(Dispatchers.IO) {
      JSON.readValue<InstallMetaRequest>(BoundedInputStream(this@withInstallMetaContext.receiveStream(), MAX_INPUT_BYTES))
    }
  } catch (e: JacksonException) {
    throw BadRequestException("Could not parse request body as JSON", e)
  }

  body.validate()

  TempFiles.withTempDirectory { workspace -> withDatabaseDetails(body.installTarget, body.meta.type) {
    fn(InstallMetaContext(
      workspace      = workspace,
      customPath     = appCtx.config.customPath,
      request        = body,
      installPath    = appCtx.pathFactory.makePath(body.installTarget, body.vdiID),
      databaseConfig = it,
      scriptConfig   = appCtx.config.installMetaScript,
    ))
  } }
}

private fun InstallMetaRequest.validate() {

  if (installTarget.isBlank())
    throw BadRequestException("projectID cannot be blank")

  if (meta.type.name.toString().isBlank())
    throw BadRequestException("type.name cannot be blank")
  if (meta.type.version.isBlank())
    throw BadRequestException("type.version cannot be blank")

  if (meta.installTargets.isEmpty())
    throw BadRequestException("projects cannot be empty")
  for (project in meta.installTargets)
    if (project.isBlank())
      throw BadRequestException("projects cannot contain a blank string")

  if (meta.name.isBlank())
    throw BadRequestException("name cannot be blank")

  for (dependency in meta.dependencies) {
    if (dependency.identifier.isBlank())
      throw BadRequestException("dependency.resourceIdentifier cannot be blank")
    if (dependency.version.isBlank())
      throw BadRequestException("dependency.resourceVersion cannot be blank")
    if (dependency.displayName.isBlank())
      throw BadRequestException("dependency.resourceDisplayName cannot be blank")
  }
}
