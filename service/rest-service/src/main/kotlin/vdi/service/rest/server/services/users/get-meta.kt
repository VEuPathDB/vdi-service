package vdi.service.rest.server.services.users

import vdi.service.rest.Options
import vdi.service.rest.generated.model.UserMetadata
import vdi.service.rest.generated.model.UserMetadataImpl
import vdi.service.rest.generated.model.UserQuotaDetails
import vdi.service.rest.generated.model.UserQuotaDetailsImpl
import vdi.service.rest.s3.DatasetStore
import vdi.lib.db.cache.CacheDB
import vdi.service.rest.server.controllers.ControllerBase

fun <T: ControllerBase> T.getUserMetadata(): UserMetadata =
  UserMetadataImpl().apply {
    quota = getUserQuotaInfo()
  }

private fun <T: ControllerBase> T.getUserQuotaInfo(): UserQuotaDetails =
  UserQuotaDetailsImpl().apply {
    usage = getCurrentQuotaUsage()
    limit = Options.Quota.quotaLimit.toLong()
  }

internal fun <T: ControllerBase> T.getCurrentQuotaUsage(): Long {
  val sizes = DatasetStore.listDatasetImportReadyZipSizes(userID)

  return CacheDB().selectUndeletedDatasetIDsForUser(userID)
    .asSequence()
    .map { sizes[it] ?: 0L }
    .sum()
}
