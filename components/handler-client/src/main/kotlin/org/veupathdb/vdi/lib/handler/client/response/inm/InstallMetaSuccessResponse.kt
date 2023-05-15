package org.veupathdb.vdi.lib.handler.client.response.inm

sealed interface InstallMetaSuccessResponse : InstallMetaResponse {
  override val type: InstallMetaResponseType
    get() = InstallMetaResponseType.Success
}
