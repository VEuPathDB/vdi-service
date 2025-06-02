@file:JvmName("DatasetDependencyValidator")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.reqCheckLength
import org.veupathdb.lib.request.validation.require
import vdi.model.data.DatasetDependency
import vdi.service.rest.generated.model.DatasetDependency as APIDependency

private const val IDMinLength = 3
private const val IDMaxLength = 50
private const val VerMinLength = 1
private const val VerMaxLength = 50

fun APIDependency.cleanup() {
  resourceIdentifier = resourceIdentifier.cleanupString()
  resourceVersion = resourceVersion.cleanupString()
}

fun List<APIDependency>.validate(jPath: String, errors: ValidationErrors) =
  forEachIndexed { i, l -> l.require(jPath, i, errors) { validate(jPath, i, errors) } }

fun APIDependency.validate(jPath: String, index: Int, errors: ValidationErrors) {
  resourceIdentifier.reqCheckLength(jPath, index, IDMinLength, IDMaxLength, errors)
  resourceVersion.reqCheckLength(jPath, index, VerMinLength, VerMaxLength, errors)
}

fun APIDependency.toInternal(): DatasetDependency =
  DatasetDependency(
    identifier  = resourceIdentifier,
    version     = resourceVersion,
    displayName = resourceDisplayName
  )
