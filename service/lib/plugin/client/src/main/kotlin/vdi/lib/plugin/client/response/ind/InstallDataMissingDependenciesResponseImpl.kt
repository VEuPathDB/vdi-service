package vdi.lib.plugin.client.response.ind

internal data class InstallDataMissingDependenciesResponseImpl(override val warnings: Collection<String>)
  : InstallDataMissingDependenciesResponse
