package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.rangeTo
import vdi.model.data.ExternalDatasetIdentifiers
import vdi.service.rest.generated.model.*

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
