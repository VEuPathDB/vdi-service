package vdi.service.rest.server.services;

import vdi.core.db.cache.CacheDbProvider;
import vdi.model.meta.UserID;
import vdi.service.rest.config.UploadConfig;
import vdi.service.rest.generated.model.UserMetadata;
import vdi.service.rest.generated.model.UserMetadataImpl;
import vdi.service.rest.generated.model.UserQuotaDetails;
import vdi.service.rest.generated.model.UserQuotaDetailsImpl;
import vdi.service.rest.s3.DatasetStore;

public class UserService {
  public static UserMetadata getUserMetadata(UserID userId, UploadConfig uploadConfig) {
    var out = new UserMetadataImpl();
    out.setQuota(getQuotaInfo(userId, uploadConfig));
    return out;
  }

  private static UserQuotaDetails getQuotaInfo(UserID userId, UploadConfig uploadConfig) {
    var out = new UserQuotaDetailsImpl();
    out.setUsage(getCurrentQuotaUsage(userId));
    out.setLimit(uploadConfig.userMaxStorageSize());
    return out;
  }

  public static long getCurrentQuotaUsage(UserID userId) {
    var sizes = DatasetStore.listDatasetImportReadyZipSizes(userId);

    return CacheDbProvider.cacheDb()
      .selectUndeletedDatasetIDsForUser(userId)
      .stream()
      .mapToLong(it -> sizes.getOrDefault(it, 0L))
      .sum();
  }
}
