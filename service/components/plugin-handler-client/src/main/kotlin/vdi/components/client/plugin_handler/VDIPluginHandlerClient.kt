package vdi.components.client.plugin_handler

import java.nio.file.Path
import vdi.components.client.plugin_handler.model.*

interface VDIPluginHandlerClient {

  fun sendImportRequest(details: ImportRequestDetails, payload: Path): ImportResponse

  fun sendInstallMetaRequest(meta: InstallMetaRequestMeta): InstallMetaResponse

  fun sendInstallDataRequest(details: InstallDataRequestDetails, payload: Path): InstallDataResponse

  fun sendUninstallRequest(details: UninstallRequest): UninstallResponse

}

