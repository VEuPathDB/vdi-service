@file:JvmName("DatasetServiceUtils")
package vdi.service.rest.server.services.dataset

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import vdi.lib.db.cache.CacheDB
import vdi.lib.db.cache.model.DatasetRecord
import vdi.lib.plugin.registry.PluginDetails
import vdi.lib.plugin.registry.PluginRegistry
import vdi.service.rest.generated.model.DatasetPatchRequestBody
import vdi.service.rest.model.UserDetails
import vdi.service.rest.server.inputs.toInternal

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

internal fun CacheDB.getLatestDatasetRecord(datasetID: DatasetID): DatasetRecord? =
  selectOriginalDatasetID(datasetID)
    .let { selectLatestRevision(it)?.revisionID ?: it }
    .let { selectDataset(it) }
