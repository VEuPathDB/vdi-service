@file:JvmName("DatasetProxyPostMetaValidator")
package vdi.service.rest.server.inputs

import com.fasterxml.jackson.module.kotlin.convertValue
import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.rangeTo
import java.net.URI
import vdi.model.data.UserID
import vdi.json.JSON
import java.time.OffsetDateTime
import vdi.model.data.DatasetMetadata
import vdi.model.data.DatasetVisibility
import vdi.service.rest.generated.model.*
import vdi.service.rest.generated.model.DatasetVisibility as APIVisibility

fun DatasetProxyPostMeta.cleanup() {
  (this as DatasetMetaBase).cleanup()
  datasetType?.cleanup()
  visibility = visibility ?: APIVisibility.PRIVATE
}

fun DatasetProxyPostMeta.validate(errors: ValidationErrors) {
  datasetType.validate(JsonField.META..JsonField.DATASET_TYPE, installTargets, errors)

  (this as DatasetMetaBase).validate(errors)
}

fun DatasetProxyPostMeta.toInternal(userID: UserID, url: String?) =
  DatasetMetadata(
    type             = datasetType.toInternal(),
    installTargets  = installTargets.toSet(),
    visibility       = visibility?.toInternal() ?: DatasetVisibility.Private,
    owner            = userID,
    name             = name,
    summary          = summary,
    origin           = origin,
    created          = createdOn ?: OffsetDateTime.now(),
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
