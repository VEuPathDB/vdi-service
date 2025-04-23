package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.*
import vdi.service.rest.generated.model.DatasetDependency
import org.veupathdb.vdi.lib.common.model.VDIDatasetDependency

private const val IDMinLength = 3
private const val IDMaxLength = 50
private const val VerMinLength = 1
private const val VerMaxLength = 50

fun vdi.service.rest.generated.model.DatasetDependency.cleanup() {
  resourceIdentifier = resourceIdentifier.cleanupString()
  resourceVersion = resourceVersion.cleanupString()
}

fun List<vdi.service.rest.generated.model.DatasetDependency>.validate(jPath: String, errors: ValidationErrors) =
  forEachIndexed { i, l -> l.require(jPath, i, errors) { validate(jPath, i, errors) } }

fun vdi.service.rest.generated.model.DatasetDependency.validate(jPath: String, index: Int, errors: ValidationErrors) {
  resourceIdentifier.reqCheckLength(jPath, index, IDMinLength, IDMaxLength, errors)
  resourceVersion.reqCheckLength(jPath, index, VerMinLength, VerMaxLength, errors)
}

fun vdi.service.rest.generated.model.DatasetDependency.toInternal(): VDIDatasetDependency =
  VDIDatasetDependency(resourceIdentifier, resourceVersion, resourceDisplayName)
