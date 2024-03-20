package vdi.component.plugin.client.response.ind

internal data class InstallDataMissingDependenciesResponseImpl(override val warnings: List<String>)
  : InstallDataMissingDependenciesResponse