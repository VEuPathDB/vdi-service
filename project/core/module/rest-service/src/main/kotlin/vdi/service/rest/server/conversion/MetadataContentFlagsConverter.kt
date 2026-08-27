package vdi.service.rest.server.conversion

import org.veupathdb.lib.request.validation.ValidationErrors
import vdi.model.meta.MetadataContentFlags
import vdi.service.rest.generated.model.MetadataContentFlagsImpl
import vdi.service.rest.generated.model.MetadataContentFlags as RamlContentFlags

object MetadataContentFlagsConverter: APITypeConverter<RamlContentFlags, MetadataContentFlags> {
  override fun cleanup(value: RamlContentFlags): RamlContentFlags {
    // nothing to do, all properties are true/false/null
    return value
  }

  override fun validate(
    value: RamlContentFlags,
    jsonPath: String,
    errors: ValidationErrors,
  ) {
    // nothing to do, all properties are true/false/null
  }

  override fun toExternal(value: MetadataContentFlags): RamlContentFlags =
    MetadataContentFlagsImpl().apply {
      hasDatasetCharacteristics = value.hasDatasetCharacteristics.asBoolean
      hasDatasetSources         = value.hasDatasetSources.asBoolean
      hasDataDisclaimer         = value.hasDataDisclaimer.asBoolean
      hasPublications           = value.hasPublications.asBoolean
      hasOrganismData           = value.hasOrganismData.asBoolean
    }

  override fun toInternal(value: RamlContentFlags) =
    MetadataContentFlags(
      hasDatasetCharacteristics = value.hasDatasetCharacteristics.toInternal(),
      hasDatasetSources         = value.hasDatasetSources.toInternal(),
      hasDataDisclaimer         = value.hasDataDisclaimer.toInternal(),
      hasPublications           = value.hasPublications.toInternal(),
      hasOrganismData           = value.hasOrganismData.toInternal(),
    )

  @Suppress("NOTHING_TO_INLINE")
  private inline fun Boolean?.toInternal() =
    MetadataContentFlags.FlagState.fromBoolean(this)
}