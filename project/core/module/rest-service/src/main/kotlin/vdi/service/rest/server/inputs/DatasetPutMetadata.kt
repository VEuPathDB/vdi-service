@file:JvmName("DatasetPutMetadataInputAdaptor")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.checkLength
import org.veupathdb.lib.request.validation.rangeTo
import org.veupathdb.lib.request.validation.reqCheckLength
import vdi.model.meta.DatasetMetadata
import vdi.service.rest.generated.model.DatasetPatchRequestBody
import vdi.service.rest.generated.model.DatasetPutMetadata
import vdi.service.rest.generated.model.JsonField


private val RevisionNoteLengthRange = 10..4096


fun DatasetPutMetadata.cleanup() {
  // Put metadata is an extension of the patch request, use the existing patch
  // request cleanup.
  (this as DatasetPatchRequestBody).cleanup()

  cleanupString(::getOrigin)
  cleanupString(::getRevisionNote)
}

fun DatasetPutMetadata.validate(original: DatasetMetadata, errors: ValidationErrors) {
  val jPath = "$"

  (this as DatasetPatchRequestBody).validate(original, jPath, errors)

  origin?.checkLength(jPath..JsonField.ORIGIN, OriginLengthRange, errors)
  revisionNote.reqCheckLength(jPath..JsonField.REVISION_NOTE, RevisionNoteLengthRange, errors)
}
