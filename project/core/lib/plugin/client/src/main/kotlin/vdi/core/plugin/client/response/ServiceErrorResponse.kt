package vdi.core.plugin.client.response

sealed interface ServiceErrorResponse
  : PluginResponse
  , ImportResponse
  , InstallDataResponse
  , InstallMetaResponse
  , UninstallResponse
{
  val message: String
}

