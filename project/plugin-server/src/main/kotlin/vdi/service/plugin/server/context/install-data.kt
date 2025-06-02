package vdi.service.plugin.server.context

import io.ktor.http.ContentType
import io.ktor.http.content.forEachPart
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.UnsupportedMediaTypeException
import io.ktor.server.request.contentType
import io.ktor.server.request.receiveMultipart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Path
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import vdi.model.api.internal.FormField
import vdi.model.api.internal.InstallDataRequest
import vdi.model.data.DatasetManifest
import vdi.model.data.DatasetMetadata
import vdi.service.plugin.model.ApplicationContext
import vdi.service.plugin.util.parseAsJson
import vdi.util.fs.TempFiles

private const val INSTALL_PAYLOAD_FILE_NAME = "install-ready.zip"
private const val INSTALL_DETAILS_MAX_SIZE = 1024uL

@Suppress("DuplicatedCode")
@OptIn(ExperimentalContracts::class)
suspend fun ApplicationCall.withInstallDataContext(
  appCtx: ApplicationContext,
  fn: suspend (installCtx: InstallDataContext) -> Unit
) {
  contract { callsInPlace(fn, InvocationKind.EXACTLY_ONCE) }

  if (!request.contentType().match(ContentType.MultiPart.FormData))
    throw UnsupportedMediaTypeException(request.contentType())

  TempFiles.withTempDirectory { workspace -> withContext(Dispatchers.IO) { withParsedRequest(appCtx, workspace, fn) } }
}

@OptIn(ExperimentalContracts::class)
private suspend fun ApplicationCall.withParsedRequest(
  appCtx: ApplicationContext,
  workspace: Path,
  fn: suspend (context: InstallDataContext) -> Unit,
) {
  contract { callsInPlace(fn, InvocationKind.EXACTLY_ONCE) }

  var details: InstallDataRequest? = null
  var payload: Path? = null
  var meta: DatasetMetadata? = null
  var manifest: DatasetManifest? = null

  receiveMultipart().forEachPart { part ->
    try {
      when (part.name) {
        FormField.Details  -> {
          reqNull(details, FormField.Details)
          details = part.parseAsJson<InstallDataRequest>(INSTALL_DETAILS_MAX_SIZE)
        }

        FormField.Metadata -> {
          reqNull(meta, FormField.Metadata)
          meta = part.parseAsJson<DatasetMetadata>(16384uL)
        }

        FormField.Manifest -> {
          reqNull(manifest, FormField.Manifest)
          manifest = part.parseAsJson<DatasetManifest>(16384uL)
        }

        FormField.Payload  -> {
          reqNull(payload, FormField.Payload)
          payload = part.handlePayload(workspace, INSTALL_PAYLOAD_FILE_NAME)
        }
      }
    } finally {
      part.dispose()
    }
  }

  reqNotNull(details, FormField.Details)
  reqNotNull(meta, FormField.Metadata)
  reqNotNull(manifest, FormField.Manifest)
  reqNotNull(payload, FormField.Payload)

  details!!.also { deets ->
    deets.validate()
    withDatabaseDetails(deets.installTarget, meta!!.type) {
      fn(InstallDataContext(
        workspace      = workspace,
        customPath     = appCtx.config.customPath,
        request        = deets,
        installPath    = appCtx.pathFactory.makePath(deets.installTarget, deets.vdiID),
        databaseConfig = it,
        metadata       = meta!!,
        payload        = payload!!,
        metaConfig     = appCtx.config.installMetaScript,
        dataConfig     = appCtx.config.installDataScript,
        compatConfig   = appCtx.config.checkCompatScript,
      ))
    }
  }
}

private fun InstallDataRequest.validate() {
  if (installTarget.isBlank()) {
    throw BadRequestException("projectID cannot be a blank value")
  }
}
