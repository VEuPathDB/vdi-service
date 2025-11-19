@file:JvmName("ExternalIdentifiersInputAdaptor")
package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.rangeTo
import vdi.model.meta.ExternalDatasetIdentifiers
import vdi.service.rest.generated.model.BioprojectIDReference
import vdi.service.rest.generated.model.DOIReference
import vdi.service.rest.generated.model.DatasetHyperlink
import vdi.service.rest.generated.model.JsonField
import vdi.service.rest.generated.model.ExternalIdentifiers as APIExternalIdentifiers

fun APIExternalIdentifiers?.cleanup() = this?.apply {
  cleanupList(::getDois, DOIReference?::cleanup)
  cleanupList(::getHyperlinks, DatasetHyperlink?::cleanup)
  cleanupList(::getBioprojectIds, BioprojectIDReference?::cleanup)
}

fun APIExternalIdentifiers.validate(jPath: String, errors: ValidationErrors) {
  dois?.validate(jPath..JsonField.DOIS, errors)
  hyperlinks?.validate(jPath..JsonField.HYPERLINKS, errors)
  bioprojectIds?.validate(jPath..JsonField.BIOPROJECT_IDS, errors)
}

fun APIExternalIdentifiers.isEmpty() =
  !dois.isNullOrEmpty() || !hyperlinks.isNullOrEmpty() || !bioprojectIds.isNullOrEmpty()

fun APIExternalIdentifiers.toInternal() =
  ExternalDatasetIdentifiers(
    dois          = dois.toInternalDistinct(DOIReference::toInternal),
    hyperlinks    = hyperlinks.toInternalDistinct(DatasetHyperlink::toInternal),
    bioprojectIDs = bioprojectIds.toInternalDistinct(BioprojectIDReference::toInternal),
  )