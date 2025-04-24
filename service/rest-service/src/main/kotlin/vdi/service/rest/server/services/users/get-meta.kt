package vdi.service.rest.server.services.users

import vdi.service.rest.config.Options
import vdi.service.rest.generated.model.UserMetadata
import vdi.service.rest.generated.model.UserMetadataImpl
import vdi.service.rest.generated.model.UserQuotaDetails
import vdi.service.rest.generated.model.UserQuotaDetailsImpl
import vdi.service.rest.s3.DatasetStore
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.lib.db.cache.CacheDB

internal fun getUserMetadata(userID: UserID): UserMetadata =
  UserMetadataImpl().apply {
    quota = getUserQuotaInfo(userID)
  }

private fun getUserQuotaInfo(userID: UserID): UserQuotaDetails =
  UserQuotaDetailsImpl().apply {
    usage = getCurrentQuotaUsage(userID)
    limit = Options.Quota.quotaLimit.toLong()
  }

internal fun getCurrentQuotaUsage(userID: UserID): Long {
  val sizes = DatasetStore.listDatasetImportReadyZipSizes(userID)

  return CacheDB().selectUndeletedDatasetIDsForUser(userID)
    .asSequence()
    .map { sizes[it] ?: 0L }
    .sum()
}
