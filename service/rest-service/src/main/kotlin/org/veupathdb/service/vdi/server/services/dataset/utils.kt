package org.veupathdb.service.vdi.server.services.dataset

import org.veupathdb.service.vdi.generated.model.DatasetPatchRequestBody
import org.veupathdb.service.vdi.model.UserDetails
import org.veupathdb.service.vdi.server.inputs.toInternal
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import vdi.lib.plugin.registry.PluginDetails
import vdi.lib.plugin.registry.PluginRegistry
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

internal fun Map<UserID, UserDetails>.requireDetails(userID: UserID) =
  get(userID) ?: throw IllegalStateException("missing user details for $userID")


@OptIn(ExperimentalContracts::class)
internal inline fun DatasetPatchRequestBody.optValidateType(onError: (PluginDetails) -> Nothing): VDIDatasetType? {
  contract { callsInPlace(onError, InvocationKind.AT_MOST_ONCE) }

  return datasetType
    ?.let { PluginRegistry[datasetType.name, datasetType.version]!! }
    ?.also { if (!it.changesEnabled) onError(it) }
    ?.let { datasetType.toInternal() }
}
