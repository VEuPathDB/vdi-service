package org.veupathdb.vdi.lib.handler.client.response.ind

sealed interface InstallDataMissingDependenciesResponse : InstallDataResponse {
  override val type: InstallDataResponseType
    get() = InstallDataResponseType.MissingDependencies

  val warnings: List<String>
}
