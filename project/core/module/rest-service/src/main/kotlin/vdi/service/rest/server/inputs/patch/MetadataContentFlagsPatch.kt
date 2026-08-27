package vdi.service.rest.server.inputs.patch

import vdi.model.meta.MetadataContentFlags
import vdi.model.meta.MetadataContentFlags.FlagState
import vdi.service.rest.generated.model.MetadataContentFlagsPatch
import vdi.service.rest.generated.model.OptionalBooleanPatch

fun MetadataContentFlagsPatch.applyPatch(previous: MetadataContentFlags) =
  MetadataContentFlags(
    hasDatasetCharacteristics = hasDatasetCharacteristics
      .orElse(previous.hasDatasetCharacteristics),
    hasDatasetSources = hasDatasetSources
      .orElse(previous.hasDatasetSources),
    hasDataDisclaimer = hasDataDisclaimer
      .orElse(previous.hasDataDisclaimer),
    hasPublications = hasPublications
      .orElse(previous.hasPublications),
    hasOrganismData = hasOrganismData
      .orElse(previous.hasOrganismData),
  )

private fun OptionalBooleanPatch?.orElse(previous: FlagState) =
  if (this == null)
    previous
  else
    FlagState.fromBoolean(value)