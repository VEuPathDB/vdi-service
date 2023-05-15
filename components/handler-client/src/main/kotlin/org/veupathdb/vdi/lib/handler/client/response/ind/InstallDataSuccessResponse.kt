package org.veupathdb.vdi.lib.handler.client.response.ind

sealed interface InstallDataSuccessResponse : InstallDataResponse {
  override val type: InstallDataResponseType
    get() = InstallDataResponseType.Success

  val warnings: List<String>
}
