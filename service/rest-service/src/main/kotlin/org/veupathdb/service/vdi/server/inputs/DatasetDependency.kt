package org.veupathdb.service.vdi.server.inputs

import org.veupathdb.lib.request.validation.*
import org.veupathdb.service.vdi.generated.model.DatasetDependency
import org.veupathdb.vdi.lib.common.model.VDIDatasetDependency

private const val IDMinLength = 3
private const val IDMaxLength = 50
private const val VerMinLength = 1
private const val VerMaxLength = 50

fun DatasetDependency.cleanup() {
  resourceIdentifier = resourceIdentifier.cleanupString()
  resourceVersion = resourceVersion.cleanupString()
}

fun List<DatasetDependency>.validate(jPath: String, errors: ValidationErrors) =
  forEachIndexed { i, l -> l.require(jPath, i, errors) { validate(jPath, i, errors) } }

fun DatasetDependency.validate(jPath: String, index: Int, errors: ValidationErrors) {
  resourceIdentifier.reqCheckLength(jPath, index, IDMinLength, IDMaxLength, errors)
  resourceVersion.reqCheckLength(jPath, index, VerMinLength, VerMaxLength, errors)
}

fun DatasetDependency.toInternal(): VDIDatasetDependency =
  VDIDatasetDependency(resourceIdentifier, resourceVersion, resourceDisplayName)
