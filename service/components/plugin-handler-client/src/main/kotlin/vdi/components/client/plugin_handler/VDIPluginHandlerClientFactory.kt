package vdi.components.client.plugin_handler

class VDIPluginHandlerClientFactory {

}

interface VDIPluginHandlerClient {

  fun sendImportRequest(): ImportResponse

  fun sendInstallMetaRequest(): InstallMetaResponse

  fun sendInstallDataRequest(): InstallDataResponse

  fun sendUninstallRequest(): UninstallResposne

}

class ImportRequestBuilder {
  private var vdiID: String? = null
  private var


}