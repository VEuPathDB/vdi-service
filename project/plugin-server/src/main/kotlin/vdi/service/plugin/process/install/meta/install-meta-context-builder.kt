package vdi.service.plugin.process.install.meta

import io.ktor.http.ContentType
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.UnsupportedMediaTypeException
import io.ktor.server.request.contentType
import io.ktor.server.request.receiveMultipart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Path
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import vdi.io.plugin.requests.FormField
import vdi.io.plugin.requests.InstallMetaRequest
import vdi.service.plugin.model.ApplicationContext
import vdi.service.plugin.server.context.*
import vdi.service.plugin.util.parseAsJson

// Max allowed size of the post body: 32KiB
private const val MAX_INPUT_BYTES = 32768uL

internal suspend fun ApplicationCall.withInstallMetaContext(
  appCtx: ApplicationContext,
  fn: suspend InstallMetaContext.() -> Unit,
) {
  if (!request.contentType().match(ContentType.Application.Json))
    throw UnsupportedMediaTypeException(request.contentType())

  withDoubleTempDirs { workspace, propsDir -> withContext(Dispatchers.IO) {
    withParsedRequest(appCtx, workspace, propsDir, fn)
  } }
}

@OptIn(ExperimentalContracts::class)
private suspend fun ApplicationCall.withParsedRequest(
  appCtx: ApplicationContext,
  workspace: Path,
  propsDir: Path,
  fn: suspend (context: InstallMetaContext) -> Unit,
) {
  contract { callsInPlace(fn, InvocationKind.EXACTLY_ONCE) }

  var details: InstallMetaRequest? = null

  receiveMultipart().forEachPart { part ->
    try {
      when (part.name) {
        FormField.Details -> {
          reqNull(details, FormField.Details)
          details = part.parseAsJson(MAX_INPUT_BYTES)
        }

        FormField.DataDict -> {
          part.handlePayload(propsDir, (part as PartData.FileItem).originalFileName!!)
        }
      }
    } finally {
      part.dispose
    }
  }

  reqNotNull(details, FormField.Metadata)

  withDatabaseDetails(details.installTarget, details.meta.type) {
    fn(InstallMetaContext(
      pluginName         = appCtx.config.name,
      workspace          = workspace,
      customPath         = appCtx.config.customPath,
      request            = details,
      installPath        = appCtx.pathFactory.makePath(details.installTarget, details.vdiID),
      databaseConfig     = it,
      scriptConfig       = appCtx.config.installMetaScript,
      dataPropertiesPath = propsDir,
    ))
  }
}
