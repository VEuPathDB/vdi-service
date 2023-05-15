package org.veupathdb.vdi.lib.handler.client.response.ind

internal data class InstallDataMissingDependenciesResponseImpl(override val warnings: List<String>)
  : InstallDataMissingDependenciesResponse