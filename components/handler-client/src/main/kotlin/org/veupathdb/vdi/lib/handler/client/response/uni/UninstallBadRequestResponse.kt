package org.veupathdb.vdi.lib.handler.client.response.uni

sealed interface UninstallBadRequestResponse : UninstallResponse {
  override val type: UninstallResponseType
    get() = UninstallResponseType.BadRequest

  val message: String
}
