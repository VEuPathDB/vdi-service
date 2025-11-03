package vdi.core.plugin.client.response.ind

internal data class InstallDataMissingDependenciesResponseImpl(override val warnings: Collection<String>)
  : InstallDataMissingDependenciesResponse
