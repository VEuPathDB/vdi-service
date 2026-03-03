package vdi.service.rest.server.services;

import org.veupathdb.lib.container.jaxrs.providers.UserProvider;
import vdi.core.db.cache.model.DatasetShareListEntry;
import vdi.core.plugin.registry.PluginRegistry;
import vdi.model.meta.UserID;
import vdi.service.rest.config.UploadConfig;
import vdi.service.rest.conversion.DatasetOwner;
import vdi.service.rest.conversion.EnumTranslator;
import vdi.service.rest.generated.model.*;
import vdi.service.rest.model.UserDetails;
import vdi.service.rest.s3.DatasetStore;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static vdi.core.db.cache.CacheDbProvider.cacheDb;

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

    return cacheDb()
      .selectUndeletedDatasetIDsForUser(userId)
      .stream()
      .mapToLong(it -> sizes.getOrDefault(it, 0L))
      .sum();
  }

  // region Shares

  public static List<ShareOfferEntry> lookupShares(UserID userId, UsersSelfShareOffersGetStatus status) {
    return switch (status) {
      case OPEN -> convertToOutType(cacheDb().selectOpenSharesForUser(userId));
      case ACCEPTED -> convertToOutType(cacheDb().selectAcceptedSharesForUser(userId));
      case REJECTED -> convertToOutType(cacheDb().selectRejectedSharesForUser(userId));
      case ALL -> convertToOutType(cacheDb().selectAllSharesForUser(userId));
    };
  }

  private static List<ShareOfferEntry> convertToOutType(Collection<DatasetShareListEntry> shares) {
    var ownerIds = shares.stream()
      .map(DatasetShareListEntry::getOwnerID)
      .collect(Collectors.toSet());

    var owners = UserProvider.getUsersById(ownerIds.stream().map(UserID::toLong).toList())
      .entrySet()
      .stream()
      .collect(Collectors.toMap(
        entry -> UserID.newUserID(entry.getKey()),
        entry -> new UserDetails(entry.getValue()))
      );

    return shares.stream().map(it -> {
      var type = new DatasetTypeOutputImpl();
      type.setName(it.getType().getNameAsString());
      type.setVersion(it.getType().getVersion());
      type.setCategory(PluginRegistry.require(it.getType()).getCategory());

      var out = new ShareOfferEntryImpl();
      out.setDatasetId(it.getDatasetIdString());
      out.setShareStatus(EnumTranslator.toExternal(it.getReceiptStatus()));
      out.setType(type);
      out.setOwner(new DatasetOwner(owners.get(it.getOwnerID())));
      out.setInstallTargets(it.getProjects());
      return (ShareOfferEntry) out;
    }).toList();
  }

  // endregion Shares
}
