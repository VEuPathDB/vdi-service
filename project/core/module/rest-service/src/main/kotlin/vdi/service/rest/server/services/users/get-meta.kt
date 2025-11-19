@file:JvmName("UserMetaService")
package vdi.service.rest.server.services.users

import vdi.core.db.cache.CacheDB
import vdi.model.meta.UserID
import vdi.service.rest.config.UploadConfig
import vdi.service.rest.generated.model.UserMetadata
import vdi.service.rest.generated.model.UserMetadataImpl
import vdi.service.rest.generated.model.UserQuotaDetails
import vdi.service.rest.generated.model.UserQuotaDetailsImpl
import vdi.service.rest.s3.DatasetStore
import vdi.service.rest.server.controllers.ControllerBase

fun <T: ControllerBase> T.getUserMetadata(uploadConfig: UploadConfig): UserMetadata =
  UserMetadataImpl().apply {
    quota = getUserQuotaInfo(userID, uploadConfig)
  }

private fun getUserQuotaInfo(userID: UserID, uploadConfig: UploadConfig): UserQuotaDetails =
  UserQuotaDetailsImpl().apply {
    usage = getCurrentQuotaUsage(userID)
    limit = uploadConfig.userMaxStorageSize.toLong()
  }

internal fun getCurrentQuotaUsage(userID: UserID): Long {
  val sizes = DatasetStore.listDatasetImportReadyZipSizes(userID)

  return CacheDB().selectUndeletedDatasetIDsForUser(userID)
    .asSequence()
    .map { sizes[it] ?: 0L }
    .sum()
}
