@file:JvmName("DatasetProxyPostMetaValidator")
package vdi.service.rest.server.inputs

import com.fasterxml.jackson.module.kotlin.convertValue
import org.veupathdb.lib.request.validation.ValidationErrors
import org.veupathdb.lib.request.validation.rangeTo
import vdi.model.data.UserID
import vdi.model.data.DatasetMeta
import vdi.json.JSON
import java.time.OffsetDateTime
import vdi.service.rest.generated.model.*

fun DatasetProxyPostMeta.cleanup() {
  (this as DatasetMetaBase).cleanup()
  datasetType?.cleanup()
  visibility = visibility ?: DatasetVisibility.PRIVATE
}

fun DatasetProxyPostMeta.validate(errors: ValidationErrors) {
  datasetType.validate(JsonField.META..JsonField.DATASET_TYPE, installTargets, errors)

  (this as DatasetMetaBase).validate(errors)
}

fun DatasetProxyPostMeta.toInternal(userID: UserID, url: String?) =
  DatasetMeta(
    type             = datasetType.toInternal(),
    installTargets         = installTargets.toSet(),
    owner            = userID,
    name             = name,
    shortName        = shortName,
    shortAttribution = shortAttribution,
    summary          = summary,
    description      = description,
    visibility       = visibility?.toInternal() ?: DatasetVisibility.Private,
    origin           = origin,
    sourceURL        = url,
    created          = createdOn ?: OffsetDateTime.now(),
    dependencies     = dependencies.map(DatasetDependency::toInternal),
    publications     = publications.map(DatasetPublication::toInternal),
    hyperlinks       = hyperlinks.map(DatasetHyperlink::toInternal),
    contacts         = contacts.map(DatasetContact::toInternal),
    organisms        = organisms,
    properties       = JSON.convertValue(properties)
  )
