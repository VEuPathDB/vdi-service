@file:JvmName("LinkedDatasetInputAdaptor")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.reqCheckLength
import org.veupathdb.lib.request.validation.rangeTo
import org.veupathdb.lib.request.validation.require
import java.net.URI
import vdi.model.data.LinkedDataset
import vdi.service.rest.generated.model.JsonField
import vdi.service.rest.generated.model.LinkedDataset as APILinkedDataset

fun APILinkedDataset?.cleanup() = this?.apply {
  cleanupString(::getDatasetUri)
  sharesRecords = sharesRecords ?: false
}

fun APILinkedDataset.validate(jPath: String, index: Int, errors: ValidationErrors) {
  val path = jPath..JsonField.DATASET_URI

  datasetUri.reqCheckLength(path, index, 7, 1024, errors)
  if (datasetUri != null) {
    try {
      URI(datasetUri).toURL()
    } catch (e: Throwable) {
      errors.add(path..index, "invalid study uri: ${e.message}")
    }
  }
}

fun List<APILinkedDataset?>.validate(jPath: String, errors: ValidationErrors) =
  forEachIndexed { i, row -> row.require(jPath, i, errors) { validate(jPath, i, errors) } }

fun APILinkedDataset.toInternal() = LinkedDataset(URI(datasetUri), sharesRecords)
