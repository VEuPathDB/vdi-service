package org.veupathdb.service.vdi.service.users

import org.veupathdb.service.vdi.config.Options
import org.veupathdb.service.vdi.generated.model.UserMetadata
import org.veupathdb.service.vdi.generated.model.UserMetadataImpl
import org.veupathdb.service.vdi.generated.model.UserQuotaDetails
import org.veupathdb.service.vdi.generated.model.UserQuotaDetailsImpl
import org.veupathdb.service.vdi.s3.DatasetStore
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.db.cache.CacheDB

internal fun getUserMetadata(userID: UserID): UserMetadata {
  return UserMetadataImpl().apply {
    quota = getUserQuotaInfo(userID)
  }
}

private fun getUserQuotaInfo(userID: UserID): UserQuotaDetails {
  return UserQuotaDetailsImpl().apply {
    usage = getCurrentQuotaUsage(userID).toLong()
    limit = Options.Quota.quotaLimit.toLong()
  }
}

internal fun getCurrentQuotaUsage(userID: UserID): ULong {
  return CacheDB.selectDatasetsForUser(userID)
    .asSequence()
    .filter { !it.isDeleted }
    .map { DatasetStore.getUploadsSize(it.ownerID, it.datasetID) }
    .sum()
}