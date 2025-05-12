@file:JvmName("DatasetPostMetaValidator")
package vdi.service.rest.server.inputs

import com.fasterxml.jackson.module.kotlin.convertValue
import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.rangeTo
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.json.JSON
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
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
  if (!projects.isNullOrEmpty()) {
    projectIds = projects
  }

  (this as DatasetMetaBase).cleanup()

  datasetType?.cleanup()
  visibility = visibility ?: DatasetVisibility.PRIVATE
}

internal fun DatasetPostMeta.validate(errors: ValidationErrors) {
  datasetType.validate(JF.META..JF.DATASET_TYPE, projectIds, errors)
  // visibility -enum, no validation needed)

  (this as DatasetMetaBase).validate(errors)
}

internal fun DatasetPostMeta.toInternal(userID: UserID, url: String?) =
  VDIDatasetMeta(
    type             = datasetType.toInternal(),
    projects         = projectIds.toSet(),
    owner            = userID,
    name             = name,
    shortName        = shortName,
    shortAttribution = shortAttribution,
    category         = category,
    summary          = summary,
    description      = description,
    visibility       = visibility!!.toInternal(),
    origin           = origin,
    sourceURL        = url,
    created          = OffsetDateTime.now(ZoneOffset.UTC),
    dependencies     = dependencies.map(DatasetDependency::toInternal),
    publications     = publications.map(DatasetPublication::toInternal),
    hyperlinks       = hyperlinks.map(DatasetHyperlink::toInternal),
    contacts         = contacts.map(DatasetContact::toInternal),
    organisms        = organisms,
    properties       = JSON.convertValue(properties)
  )
