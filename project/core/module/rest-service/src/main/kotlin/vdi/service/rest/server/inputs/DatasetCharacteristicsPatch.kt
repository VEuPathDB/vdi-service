package vdi.service.rest.server.inputs

import vdi.model.meta.DatasetCharacteristics
import vdi.service.rest.generated.model.DatasetCharacteristicsPatch
import vdi.service.rest.generated.model.SampleYearRange

fun DatasetCharacteristicsPatch?.applyPatch(original: DatasetCharacteristics?) =
  when (this) {
    null -> original
    else -> DatasetCharacteristics(
      studyDesign       = studyDesign.unsafePatch(original?.studyDesign),
      studyType         = studyType.unsafePatch(original?.studyType),
      countries         = countries.unsafePatch(original?.countries ?: emptyList()),
      years             = years.unsafePatch(original?.years, SampleYearRange::toInternal),
      studySpecies      = studySpecies.unsafePatch(original?.studySpecies ?: emptyList()),
      diseases          = diseases.unsafePatch(original?.diseases ?: emptyList()),
      associatedFactors = associatedFactors.unsafePatch(original?.associatedFactors ?: emptyList()),
      participantAges   = participantAges.unsafePatch(original?.participantAges),
      sampleTypes       = sampleTypes.unsafePatch(original?.sampleTypes ?: emptyList())
    )
  }

/**
 * Tests if the patch object contains any property overwrite values.
 */
internal fun DatasetCharacteristicsPatch.hasSomethingToUpdate() =
  studyDesign != null
  || studyType != null
  || countries != null
  || years != null
  || studySpecies != null
  || diseases != null
  || associatedFactors != null
  || participantAges != null
  || sampleTypes != null