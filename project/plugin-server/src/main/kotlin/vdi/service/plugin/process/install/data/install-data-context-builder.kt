package vdi.service.plugin.process.install.data

import io.ktor.http.ContentType
import io.ktor.http.content.PartData
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
import vdi.io.plugin.requests.FormField
import vdi.io.plugin.requests.InstallDataRequest
import vdi.model.meta.DatasetManifest
import vdi.model.meta.DatasetMetadata
import vdi.service.plugin.model.ApplicationContext
import vdi.service.plugin.server.context.*
import vdi.service.plugin.util.parseAsJson

private const val INSTALL_PAYLOAD_FILE_NAME = "install-ready.zip"
private const val INSTALL_DETAILS_MAX_SIZE = 1024uL

@Suppress("DuplicatedCode")
internal suspend fun ApplicationCall.withInstallDataContext(
  appCtx: ApplicationContext,
  fn: suspend InstallDataContext.() -> Unit,
) {
  if (!request.contentType().match(ContentType.MultiPart.FormData))
    throw UnsupportedMediaTypeException(request.contentType())

  withDoubleTempDirs { workspace, propsPath -> withContext(Dispatchers.IO) {
    withParsedRequest(appCtx, workspace, propsPath, fn)
  } }
}

@OptIn(ExperimentalContracts::class)
private suspend fun ApplicationCall.withParsedRequest(
  appCtx:    ApplicationContext,
  workspace: Path,
  propsDir:  Path,
  fn:        suspend (context: InstallDataContext) -> Unit,
) {
  contract { callsInPlace(fn, InvocationKind.EXACTLY_ONCE) }

  var details:  InstallDataRequest? = null
  var payload:  Path? = null
  var meta:     DatasetMetadata? = null
  var manifest: DatasetManifest? = null

  receiveMultipart().forEachPart { part ->
    try {
      when (part.name) {
        FormField.Details -> {
          reqNull(details, FormField.Details)
          details = part.parseAsJson(INSTALL_DETAILS_MAX_SIZE)
        }

        FormField.Metadata -> {
          reqNull(meta, FormField.Metadata)
          meta = part.parseAsJson(16384uL)
        }

        FormField.Manifest -> {
          reqNull(manifest, FormField.Manifest)
          manifest = part.parseAsJson(16384uL)
        }

        FormField.Payload -> {
          reqNull(payload, FormField.Payload)
          payload = part.handlePayload(workspace, INSTALL_PAYLOAD_FILE_NAME)
        }

        FormField.DataDict -> {
          part.handlePayload(propsDir, (part as PartData.FileItem).originalFileName!!)
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

  details.also { deets ->
    deets.validate()
    withDatabaseDetails(deets.installTarget, meta.type) {
      fn(InstallDataContext(
        pluginName         = appCtx.config.name,
        workspace          = workspace,
        customPath         = appCtx.config.customPath,
        request            = deets,
        installPath        = appCtx.pathFactory.makePath(deets.installTarget, deets.vdiID),
        databaseConfig     = it,
        metadata           = meta,
        payload            = payload,
        metaConfig         = appCtx.config.installMetaScript,
        dataConfig         = appCtx.config.installDataScript,
        compatConfig       = appCtx.config.checkCompatScript,
        dataPropertiesPath = propsDir,
      ))
    }
  }
}

private fun InstallDataRequest.validate() {
  if (installTarget.isBlank()) {
    throw BadRequestException("projectID cannot be a blank value")
  }
}
