package vdi.lib.plugin.client.response.ind

sealed interface InstallDataMissingDependenciesResponse : InstallDataResponse {
  override val type: InstallDataResponseType
    get() = InstallDataResponseType.MissingDependencies

  val warnings: Collection<String>
}
