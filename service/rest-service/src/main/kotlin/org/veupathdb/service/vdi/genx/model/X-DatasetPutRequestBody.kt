package org.veupathdb.service.vdi.genx.model

import org.veupathdb.service.vdi.generated.model.DatasetPatchRequestBody
import org.veupathdb.service.vdi.generated.model.DatasetPutMetadata
import org.veupathdb.service.vdi.generated.model.DatasetPutRequestBody
import org.veupathdb.service.vdi.util.ValidationErrors

private const val RevisionNoteMinLength = 10
private const val RevisionNoteMaxLength = 4096

internal fun DatasetPutRequestBody.cleanup() {
  meta.cleanup()
  url = url.takeUnless { it.isNullOrBlank() }
}

internal fun DatasetPutRequestBody.validate() {

}

private fun DatasetPutMetadata.cleanup() {
  // Put metadata is an extension of the patch request
  (this as DatasetPatchRequestBody).cleanup()

  // Put meta specific fields
  revisionNote = revisionNote.takeUnless { it.isNullOrBlank() }
}

private fun DatasetPutMetadata.validate(errors: ValidationErrors) {
  (this as DatasetPatchRequestBody).validate(errors)

  when {
    revisionNote.isNullOrBlank() ->
      errors.add("revisionNote", "revision note is required")
    revisionNote.length < RevisionNoteMinLength ->
      errors.add("revisionNote", "revision note must be at least 10 characters")
    revisionNote.length > RevisionNoteMaxLength ->
      errors.add("revisionNote", "revision note cannot be longer than 4096 characters")
  }
}
