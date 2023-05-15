package org.veupathdb.vdi.lib.handler.client.response.ind

sealed interface InstallDataBadRequestResponse : InstallDataResponse {
  override val type: InstallDataResponseType
    get() = InstallDataResponseType.BadRequest

  val message: String
}
