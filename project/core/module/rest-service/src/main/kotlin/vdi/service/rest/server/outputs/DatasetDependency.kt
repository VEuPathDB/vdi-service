package vdi.service.rest.server.outputs

import vdi.model.data.DatasetDependency
import vdi.service.rest.generated.model.DatasetDependency as APIDependency
import vdi.service.rest.generated.model.DatasetDependencyImpl

internal fun DatasetDependency(dependency: DatasetDependency): APIDependency =
  DatasetDependencyImpl().also {
    it.resourceDisplayName = dependency.displayName
    it.resourceIdentifier  = dependency.identifier
    it.resourceVersion     = dependency.version
  }

internal fun DatasetDependency(
  resourceIdentifier: String,
  resourceDisplayName: String,
  resourceVersion: String,
): APIDependency =
  DatasetDependencyImpl().also {
    it.resourceIdentifier = resourceIdentifier
    it.resourceDisplayName = resourceDisplayName
    it.resourceVersion = resourceVersion
  }

internal fun DatasetDependencies(dependencies: Collection<DatasetDependency>) =
  dependencies.map { DatasetDependency(
    resourceIdentifier = it.identifier,
    resourceDisplayName = it.displayName,
    resourceVersion = it.version,
  ) }
