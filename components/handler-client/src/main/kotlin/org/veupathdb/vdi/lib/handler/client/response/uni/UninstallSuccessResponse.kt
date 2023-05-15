package org.veupathdb.vdi.lib.handler.client.response.uni

sealed interface UninstallSuccessResponse : UninstallResponse {
  override val type: UninstallResponseType
    get() = UninstallResponseType.Success
}
