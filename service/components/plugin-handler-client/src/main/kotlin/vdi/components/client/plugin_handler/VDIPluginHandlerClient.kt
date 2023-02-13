package vdi.components.client.plugin_handler

import java.nio.file.Path
import vdi.components.client.plugin_handler.model.*

interface VDIPluginHandlerClient {

  /**
   * Submits an import request to the target VDI Plugin Handler Server.
   *
   * @param details Details about the dataset to be import processed.
   *
   * @param payload Path to the payload file that will be uploaded to the VDI
   * plugin handler server.
   */
  fun sendImportRequest(details: ImportRequestDetails, payload: Path): ImportResponse

  fun sendInstallMetaRequest(meta: InstallMetaRequestMeta): InstallMetaResponse

  fun sendInstallDataRequest(details: InstallDataRequestDetails, payload: Path): InstallDataResponse

  fun sendUninstallRequest(details: UninstallRequest): UninstallResponse

}

