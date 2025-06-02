@file:JvmName("DatasetServiceUtils")
package vdi.service.rest.server.services.dataset

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import vdi.core.plugin.registry.PluginDetails
import vdi.core.plugin.registry.PluginRegistry
import vdi.model.data.DatasetType
import vdi.model.data.UserID
import vdi.service.rest.generated.model.DatasetPatchRequestBody
import vdi.service.rest.model.UserDetails
import vdi.service.rest.server.inputs.toInternal

internal fun Map<UserID, UserDetails>.requireDetails(userID: UserID) =
  get(userID) ?: throw IllegalStateException("missing user details for $userID")


@OptIn(ExperimentalContracts::class)
internal inline fun DatasetPatchRequestBody.optValidateType(onError: (PluginDetails) -> Nothing): DatasetType? {
  contract { callsInPlace(onError, InvocationKind.AT_MOST_ONCE) }

  return datasetType?.toInternal()
    ?.also { PluginRegistry[it]!!.also { p -> if (!p.changesEnabled) onError(p) } }
}
