package vdi.service.server.services.users

import vdi.service.config.Options
import vdi.service.generated.model.UserMetadata
import vdi.service.generated.model.UserMetadataImpl
import vdi.service.generated.model.UserQuotaDetails
import vdi.service.generated.model.UserQuotaDetailsImpl
import vdi.service.s3.DatasetStore
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.component.db.cache.CacheDB

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
