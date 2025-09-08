@file:JvmName("DatasetPostMetaInputAdaptor")

package vdi.service.rest.server.inputs

import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.rangeTo
import java.net.URI
import vdi.model.data.UserID
import java.time.OffsetDateTime
import java.time.ZoneOffset
import vdi.model.data.DatasetMetadata
import vdi.service.rest.generated.model.*
import vdi.service.rest.generated.model.JsonField as JF

/**
 * Property cleanup, trim strings, remove blanks, replace empty/null lists with
 * the empty list singleton.
 *
 * For sanity's sake properties in this method should try and keep the same
 * ordering as the property definitions in the API docs.
 */
@Suppress("DuplicatedCode") // Overlap in generated API types
internal fun DatasetPostMeta.cleanup() {
  (this as DatasetMetaBase).cleanup()

  cleanup(::getType, DatasetTypeInput?::cleanup)
  ensureNotNull(::getVisibility, DatasetVisibility.PRIVATE)
}

internal fun DatasetPostMeta.validate(errors: ValidationErrors) {
  type.validate(JF.META..JF.DATASET_TYPE, installTargets, errors)
  (this as DatasetMetaBase).validate(visibility == DatasetVisibility.PUBLIC, errors)
}

internal fun DatasetPostMeta.toInternal(userID: UserID, url: String?) =
  DatasetMetadata(
    type                 = type.toInternal(),
    installTargets       = installTargets.toSet(),
    visibility           = visibility.toInternal(),
    owner                = userID,
    name                 = name,
    summary              = summary,
    description          = description,
    origin               = origin,
    created              = OffsetDateTime.now(ZoneOffset.UTC),
    sourceURL            = url?.let(URI::create),
    dependencies         = dependencies.toInternalDistinct(DatasetDependency::toInternal),
    publications         = publications.toInternalDistinct(DatasetPublication::toInternal),
    contacts             = contacts.toInternalDistinct(DatasetContact::toInternal),
    projectName          = projectName,
    programName          = programName,
    linkedDatasets       = linkedDatasets.toInternalDistinct(LinkedDataset::toInternal),
    experimentalOrganism = experimentalOrganism.toInternal(),
    hostOrganism         = hostOrganism.toInternal(),
    characteristics = studyCharacteristics.toInternal(),
    externalIdentifiers  = externalIdentifiers.toInternal(),
    funding              = funding.toInternalDistinct(DatasetFundingAward::toInternal),
  )
