package org.veupathdb.service.vdi.service.users

import org.veupathdb.service.vdi.config.Options
import org.veupathdb.service.vdi.generated.model.UserMetadata
import org.veupathdb.service.vdi.generated.model.UserMetadataImpl
import org.veupathdb.service.vdi.generated.model.UserQuotaDetails
import org.veupathdb.service.vdi.generated.model.UserQuotaDetailsImpl
import org.veupathdb.service.vdi.s3.DatasetStore
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
