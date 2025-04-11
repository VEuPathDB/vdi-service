package org.veupathdb.service.vdi.server.inputs

import jakarta.ws.rs.BadRequestException
import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.reqCheckLength
import org.veupathdb.service.vdi.generated.model.DatasetPatchRequestBody
import org.veupathdb.service.vdi.generated.model.DatasetPutMetadata
import org.veupathdb.service.vdi.generated.model.DatasetPutRequestBody
import org.veupathdb.service.vdi.generated.model.JsonField

private const val RevisionNoteMinLength = 10
private const val RevisionNoteMaxLength = 4096

fun DatasetPutRequestBody.cleanup() {
  meta.cleanup()
  url = url.takeUnless { it.isNullOrBlank() }
}

private fun DatasetPutMetadata.cleanup() {
  // Put metadata is an extension of the patch request
  (this as DatasetPatchRequestBody).cleanup()

  // Put meta specific fields
  revisionNote = revisionNote.cleanupString()
}

@Suppress("DuplicatedCode") // overlap in generated API pojo field names
fun DatasetPutRequestBody.validate(): ValidationErrors {
  // DatasetPutRequestBody is not a JSON object, it is a multipart/form-data
  // request.  The "fields" are form parts and are not validated as if they are
  // part of a syntactically correct JSON body.

  // If the meta field is null, then the request body was incomplete.
  if (meta == null)
    throw BadRequestException("missing dataset metadata")

  // If there is no file or URL, then the request body was incomplete.
  // If there is a file AND URL, then the request body is invalid.
  if (file == null) {
    if (url == null)
      throw BadRequestException("must provide an upload file or url to a source file")
  } else if (url != null) {
    throw BadRequestException("cannot provide both an upload file and a source file URL")
  }

  return ValidationErrors().also { meta.validate(it) }
}

private fun DatasetPutMetadata.validate(errors: ValidationErrors) {
  (this as DatasetPatchRequestBody).validate(errors)

  revisionNote.reqCheckLength(JsonField.REVISION_NOTE, RevisionNoteMinLength, RevisionNoteMaxLength, errors)
}
