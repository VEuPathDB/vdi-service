@file:JvmName("DatasetPutMetadataInputAdaptor")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.reqCheckLength
import vdi.model.data.InstallTargetID
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

fun DatasetPutMetadata.validate(projects: Iterable<InstallTargetID>, errors: ValidationErrors) {
  (this as DatasetPatchRequestBody).validate(projects, errors)

  origin.validateOrigin(JsonField.ORIGIN, errors)
  revisionNote.reqCheckLength(JsonField.REVISION_NOTE, RevisionNoteLengthRange, errors)
}
