package vdi.service.rest.services;

import vdi.core.db.cache.CacheDBManager;
import vdi.model.meta.UserID;
import vdi.service.rest.config.UploadConfig;
import vdi.service.rest.generated.model.UserMetadata;
import vdi.service.rest.generated.model.UserMetadataImpl;
import vdi.service.rest.generated.model.UserQuotaDetails;
import vdi.service.rest.generated.model.UserQuotaDetailsImpl;
import vdi.service.rest.s3.DatasetStore;

public final class UserMetaService {
  public static UserMetadata getUserMetadata(UserID userId, UploadConfig uploadConfig) {
    return new UserMetadataImpl() {{
      setQuota(getUserQuotaDetails(userId, uploadConfig));
    }};
  }

  private static UserQuotaDetails getUserQuotaDetails(UserID userId, UploadConfig uploadConfig) {
    return new UserQuotaDetailsImpl() {{
      setUsage(getCurrentQuotaUsage(userId));
      setLimit(uploadConfig.userMaxStorageSize());
    }};
  }

  public static long getCurrentQuotaUsage(UserID userId) {
    var sizes = DatasetStore.listDatasetImportReadyZipSizes(userId);

    return CacheDBManager.getInstance()
      .selectUndeletedDatasetIDsForUser(userId)
      .stream()
      .mapToLong(it -> sizes.getOrDefault(it, 0L))
      .sum();
  }
}
