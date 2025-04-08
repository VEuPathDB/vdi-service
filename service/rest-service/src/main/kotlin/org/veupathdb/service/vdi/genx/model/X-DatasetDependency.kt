package org.veupathdb.service.vdi.genx.model

import org.veupathdb.service.vdi.generated.model.DatasetDependency
import org.veupathdb.service.vdi.generated.model.DatasetDependencyImpl
import org.veupathdb.vdi.lib.common.model.VDIDatasetDependency

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
