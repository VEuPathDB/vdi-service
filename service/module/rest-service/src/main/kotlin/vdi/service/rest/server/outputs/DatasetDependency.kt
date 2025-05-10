package vdi.service.rest.server.outputs

import org.veupathdb.vdi.lib.common.model.VDIDatasetDependency
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

internal fun DatasetDependencies(dependencies: Collection<VDIDatasetDependency>) =
  dependencies.map { DatasetDependency(
    resourceIdentifier = it.identifier,
    resourceDisplayName = it.displayName,
    resourceVersion = it.version,
  ) }
