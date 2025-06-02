package vdi.service.rest.server.outputs

import vdi.model.data.DatasetDependency
import vdi.service.rest.generated.model.DatasetDependency
import vdi.service.rest.generated.model.DatasetDependencyImpl

internal fun DatasetDependency(
  resourceIdentifier: String,
  resourceDisplayName: String,
  resourceVersion: String,
): DatasetDependency =
  DatasetDependencyImpl().also {
    it.resourceIdentifier = resourceIdentifier
    it.resourceDisplayName = resourceDisplayName
    it.resourceVersion = resourceVersion
  }

internal fun DatasetDependencies(dependencies: Collection<vdi.model.data.DatasetDependency>) =
  dependencies.map { DatasetDependency(
    resourceIdentifier = it.identifier,
    resourceDisplayName = it.displayName,
    resourceVersion = it.version,
  ) }
