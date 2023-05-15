package org.veupathdb.vdi.lib.handler.client.response.inm

sealed interface InstallMetaBadRequestResponse : InstallMetaResponse {
  override val type: InstallMetaResponseType
    get() = InstallMetaResponseType.BadRequest

  val message: String
}
