@file:JvmName("RelatedStudyApiExtensions")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.reqCheckLength
import org.veupathdb.lib.request.validation.rangeTo
import org.veupathdb.lib.request.validation.require
import java.net.URI
import vdi.model.data.RelatedStudy
import vdi.service.rest.generated.model.JsonField
import vdi.service.rest.generated.model.RelatedStudy as APIRelatedStudy

fun APIRelatedStudy?.cleanup() = this?.apply {
  cleanupString(::getStudyUri)
  sharesRecords = sharesRecords ?: false
}

fun APIRelatedStudy.validate(jPath: String, index: Int, errors: ValidationErrors) {
  val path = jPath..JsonField.STUDY_URI

  studyUri.reqCheckLength(path, index, 7, 1024, errors)
  if (studyUri != null) {
    try {
      URI(studyUri).toURL()
    } catch (e: Throwable) {
      errors.add(path..index, "invalid study uri: ${e.message}")
    }
  }
}

fun List<APIRelatedStudy?>.validate(jPath: String, errors: ValidationErrors) =
  forEachIndexed { i, row -> row.require(jPath, i, errors) { validate(jPath, i, errors) } }

fun APIRelatedStudy.toInternal() = RelatedStudy(URI(studyUri), sharesRecords)
