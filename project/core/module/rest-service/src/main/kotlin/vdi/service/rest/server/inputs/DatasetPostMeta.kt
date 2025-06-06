@file:JvmName("DatasetPostMetaValidator")
package vdi.service.rest.server.inputs

import com.fasterxml.jackson.module.kotlin.convertValue
import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.rangeTo
import java.net.URI
import vdi.model.data.UserID
import vdi.json.JSON
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

  datasetType?.cleanup()
  visibility = visibility ?: DatasetVisibility.PRIVATE
}

internal fun DatasetPostMeta.validate(errors: ValidationErrors) {
  datasetType.validate(JF.META..JF.DATASET_TYPE, installTargets, errors)
  (this as DatasetMetaBase).validate(errors)
}

internal fun DatasetPostMeta.toInternal(userID: UserID, url: String?) =
  DatasetMetadata(
    type             = datasetType.toInternal(),
    installTargets   = installTargets.toSet(),
    visibility       = visibility!!.toInternal(),
    owner            = userID,
    name             = name,
    summary          = summary,
    origin           = origin,
    created          = OffsetDateTime.now(ZoneOffset.UTC),
    shortName        = shortName,
    description      = description,
    shortAttribution = shortAttribution,
    sourceURL        = url?.let(URI::create),
    dependencies     = dependencies.map(DatasetDependency::toInternal),
    publications     = publications.map(DatasetPublication::toInternal),
    hyperlinks       = hyperlinks.map(DatasetHyperlink::toInternal),
    organisms        = organisms.toSet(),
    contacts         = contacts.map(DatasetContact::toInternal),
    properties       = JSON.convertValue(properties)
  )
