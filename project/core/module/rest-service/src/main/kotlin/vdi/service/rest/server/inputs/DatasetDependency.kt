@file:JvmName("DatasetDependencyValidator")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.reqCheckLength
import org.veupathdb.lib.request.validation.require
import vdi.model.data.DatasetDependency
import vdi.service.rest.generated.model.DatasetDependency as APIDependency

private val IDLengthRange = 3..50
private val VersionLengthRange = 1..50

fun APIDependency?.cleanup(): APIDependency? {
  if (this != null) {
    cleanupString(::getResourceIdentifier)
    cleanupString(::getResourceVersion)
    cleanupString(::getResourceDisplayName)
  }
  return this
}

fun List<APIDependency>.validate(jPath: String, errors: ValidationErrors) =
  forEachIndexed { i, l -> l.require(jPath, i, errors) { validate(jPath, i, errors) } }

fun APIDependency.validate(jPath: String, index: Int, errors: ValidationErrors) {
  resourceIdentifier.reqCheckLength(jPath, index, IDLengthRange, errors)
  resourceVersion.reqCheckLength(jPath, index, VersionLengthRange, errors)
}

fun APIDependency.toInternal(): DatasetDependency =
  DatasetDependency(
    identifier  = resourceIdentifier,
    version     = resourceVersion,
    displayName = resourceDisplayName
  )
