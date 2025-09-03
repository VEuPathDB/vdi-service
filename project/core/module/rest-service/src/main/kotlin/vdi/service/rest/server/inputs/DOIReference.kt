@file:JvmName("DOIReferenceInputAdaptor")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.checkLength
import org.veupathdb.lib.request.validation.rangeTo
import org.veupathdb.lib.request.validation.require
import vdi.model.data.DOIReference
import vdi.service.rest.generated.model.DOIReference as APIDOIReference

val DOIPattern = Regex("^(?:doi:)?\\d+(\\.\\d+)+/.+\$", RegexOption.IGNORE_CASE)

internal val DOIValidLengthRange = 6..128

fun APIDOIReference?.cleanup() = this?.apply {
  cleanupString(::getDoi)
  cleanupString(::getDescription)
}

fun APIDOIReference.validate(jPath: String, index: Int, errors: ValidationErrors) {
  doi.require(jPath, index, errors) {
    checkLength(jPath, index, DOIValidLengthRange, errors)
    if (!DOIPattern.matches(doi))
      errors.add(jPath..index, "invalid doi format")
  }
}

fun List<APIDOIReference>.validate(jPath: String, errors: ValidationErrors) =
  forEachIndexed { i, doi -> doi.require(jPath, i, errors) { validate(jPath, i, errors) } }

fun APIDOIReference.toInternal() = DOIReference(doi, description)