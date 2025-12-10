package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.rangeTo
import vdi.model.meta.ExternalDatasetIdentifiers
import vdi.service.rest.generated.model.BioprojectIDReference
import vdi.service.rest.generated.model.DatasetHyperlink
import vdi.service.rest.generated.model.ExternalIdentifiersPatch
import vdi.service.rest.generated.model.JsonField

internal fun ExternalIdentifiersPatch?.applyPatch(original: ExternalDatasetIdentifiers?) =
  when (this) {
    null -> original
    else -> ExternalDatasetIdentifiers(
      dois          = dois.unsafePatch(original?.dois ?: emptyList()),
      hyperlinks    = hyperlinks.unsafePatch(original?.hyperlinks ?: emptyList(), Iterable<DatasetHyperlink>::toInternal),
      bioprojectIDs = bioprojectIds.unsafePatch(original?.bioprojectIDs ?: emptyList(), Iterable<BioprojectIDReference>::toInternal)
    )
  }

internal fun ExternalIdentifiersPatch.validate(jPath: String, errors: ValidationErrors) {
  dois?.value?.validate(jPath..JsonField.DOIS, errors)
  hyperlinks?.value?.validate(jPath..JsonField.HYPERLINKS, errors)
  bioprojectIds?.value?.validate(jPath..JsonField.BIOPROJECT_IDS, errors)
}

internal fun ExternalIdentifiersPatch.hasSomethingToUpdate() =
  dois != null || hyperlinks != null || bioprojectIds != null