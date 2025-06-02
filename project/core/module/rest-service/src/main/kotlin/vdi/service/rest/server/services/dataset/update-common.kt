@file:JvmName("DatasetUpdateCommon")
@file:Suppress("NOTHING_TO_INLINE")
package vdi.service.rest.server.services.dataset

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.convertValue
import vdi.model.data.DatasetID
import vdi.model.data.UserID
import vdi.model.data.DatasetMeta
import vdi.model.data.DatasetMetadata
import vdi.model.data.DatasetProperties
import vdi.model.data.DatasetRevision
import vdi.model.data.DatasetType
import vdi.json.JSON
import vdi.service.rest.generated.model.DatasetContact
import vdi.service.rest.generated.model.DatasetHyperlink
import vdi.service.rest.generated.model.DatasetPatchRequestBody
import vdi.service.rest.generated.model.DatasetPublication
import vdi.service.rest.server.inputs.toInternal

fun DatasetMetadata.applyPatch(
  userID: UserID,
  targetType: DatasetType?,
  patch: DatasetPatchRequestBody,
  originalID: DatasetID? = this.originalID,
  revisionHistory: List<DatasetRevision> = this.revisionHistory,
) =
  DatasetMeta(
    type             = targetType ?: type,
    installTargets         = installTargets,
    visibility       = patch.visibility?.toInternal() ?: visibility,
    owner            = userID,
    name             = patch.name ?: name,
    shortName        = patch.shortName.applyPatch(shortName),
    shortAttribution = patch.shortAttribution.applyPatch(shortAttribution),
    summary          = patch.summary ?: summary,
    description      = patch.description.applyPatch(description),
    origin           = origin,
    dependencies     = dependencies,
    sourceURL        = sourceURL,
    created          = created,
    publications     = patch.publications.applyPatch(publications, DatasetPublication::toInternal),
    hyperlinks       = patch.hyperlinks.applyPatch(hyperlinks, DatasetHyperlink::toInternal),
    contacts         = patch.contacts.applyPatch(contacts, DatasetContact::toInternal),
    organisms        = patch.organisms.applyPatch(organisms),
    properties       = patch.properties.applyPatch(properties),
    originalID       = originalID,
    revisionHistory  = revisionHistory,
  )

/**
 * Returns the update value for a string field based on the following rules:
 *
 * 1. If the input string is null, nothing to update, use the original value.
 * 2. If the input string is blank, set the field to null.
 * 3. If the input string has content, use the input string.
 */
private inline fun String?.applyPatch(original: String?): String? =
  when {
    // null input == don't update
    this == null -> original
    // blank input == clear value
    isEmpty() -> null
    // non-null && non-empty == update value
    else -> this
  }

/**
 * Returns the update value for a collection field based on the following rules:
 *
 * 1. If the input is null, use the original value.
 * 2. If the input is empty, clear the field.
 * 3. If the input has content, use the input.
 */
private inline fun <T> Collection<T>?.applyPatch(original: Collection<T>): Collection<T> =
  when {
    // null input == don't update
    this == null -> original
    // empty input == clear value
    isEmpty() -> emptyList()
    // non-null && non-empty == update value
    else -> this
  }

/**
 * Returns the update value for a collection field based on the following rules:
 *
 * 1. If the input is null, use the original value.
 * 2. If the input is empty, clear the field.
 * 3. If the input has content, use the input.
 */
private inline fun <E, I> Collection<E>?.applyPatch(original: Collection<I>, translate: (E) -> I): Collection<I> =
  when {
    // null input == don't update
    this == null -> original
    // empty input == clear value
    isEmpty() -> emptyList()
    // non-null && non-empty == update value
    else -> map(translate)
  }

private inline fun ObjectNode?.applyPatch(original: DatasetProperties?): DatasetProperties? =
  when {
    this == null -> original
    isEmpty      -> null
    else         -> JSON.convertValue(this)
  }
