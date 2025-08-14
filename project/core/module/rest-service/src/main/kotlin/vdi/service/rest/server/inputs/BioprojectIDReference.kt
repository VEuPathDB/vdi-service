@file:JvmName("BioprojectIDReferenceApiExtensions")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.*
import vdi.model.data.BioprojectIDReference
import vdi.service.rest.generated.model.BioprojectIDReferenceImpl
import vdi.service.rest.generated.model.JsonField
import vdi.service.rest.generated.model.BioprojectIDReference as APIBioReference

private val IDLengthRange = 4..64

fun APIBioReference?.cleanup() = this?.apply {
  cleanupString(::getId)
  cleanupString(::getDescription)
}

fun APIBioReference.validate(jPath: String, index: Int, errors: ValidationErrors) {
  id.reqCheckLength(jPath, index, IDLengthRange, errors)
}

fun List<APIBioReference>.validate(jPath: String, errors: ValidationErrors) =
  forEachIndexed { i, id -> id.require(jPath, i, errors) { validate(jPath, i, errors) } }

fun APIBioReference.toInternal() =
  BioprojectIDReference(id, description)

fun BioprojectIDReference(internalForm: BioprojectIDReference): APIBioReference =
  BioprojectIDReferenceImpl().apply {
    id = internalForm.id
    description = internalForm.description
  }

fun APIBioReference.validatePatch(og: APIBioReference, jPath: String, errors: ValidationErrors) {
  requireUnpatched(og, jPath, errors, APIBioReference::getId)
}

