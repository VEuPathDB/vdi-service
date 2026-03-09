package vdi.service.rest.conversion;

import vdi.core.db.cache.model.BrokenImportRecord;
import vdi.core.plugin.registry.PluginDatasetTypeMeta;
import vdi.core.plugin.registry.PluginRegistry;
import vdi.model.meta.DatasetRevisionHistory;
import vdi.model.meta.DatasetType;
import vdi.service.rest.generated.model.*;
import vdi.service.rest.model.UserDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public final class OutputTypes {
  public static BrokenImportDetails BrokenImportDetails(BrokenImportRecord record) {
    return new BrokenImportDetailsImpl() {{
      setDatasetId(record.datasetIdAsString());
      setOwner(record.getOwnerID().toLong());
      setDatasetType(DatasetTypeOutput(record.getType()));
      setInstallTargets(record.getInstallTargets());
      setMessages(record.getMessages());
    }};
  }

  public static DatasetListShareUser DatasetListShareUser(UserDetails user, boolean acceptedShare) {
    return new DatasetListShareUserImpl() {{
      setUserId(user.getUserID().toLong());
      setFirstName(user.getFirstName());
      setLastName(user.getLastName());
      setAffiliation(user.getOrganization());
      setAccepted(acceptedShare);
    }};
  }

  public static DatasetRevision DatasetRevision(vdi.model.meta.DatasetRevision revision) {
    return new DatasetRevisionImpl() {{
      setAction(DatasetRevisionAction(revision.getAction()));
      setRevisionId(revision.getRevisionIdString());
      setTimestamp(revision.getTimestamp());
      setRevisionNote(revision.getRevisionNote());
    }};
  }

  public static DatasetRevisionAction DatasetRevisionAction(vdi.model.meta.DatasetRevision.Action action) {
    return switch (action) {
      case null   -> null;
      case Revise -> DatasetRevisionAction.REVISE;
      case Extend -> DatasetRevisionAction.EXTEND;
      case Create -> DatasetRevisionAction.CREATE;
    };
  }

  public static DatasetTypeOutput DatasetTypeOutput(DatasetType type) {
    return new DatasetTypeOutputImpl() {{
      setName(type.getNameString());
      setVersion(type.getVersion());
      setCategory(PluginRegistry.require(type).getCategory());
    }};
  }

  public static PluginListItem PluginListItem(DatasetType type, PluginDatasetTypeMeta pluginMeta) {
    return new PluginListItemImpl() {{
      setPluginName(pluginMeta.getPlugin());
      setInstallTargets(Arrays.asList(Objects.requireNonNull(pluginMeta.getInstallTargets())));
      setDataTypes(new ArrayList<>(8) {{
        add(PluginDataType(type));
      }});
    }};
  }

  public static PluginDataType PluginDataType(DatasetType type) {
    var config = PluginRegistry.require(type);

    return new PluginDataTypeImpl() {{
      setName(type.getNameString());
      setVersion(type.getVersion());
      setCategory(config.getCategory());
      setUsesDataProperties(config.getUsesDataPropertiesFiles());
      setMaxFileSize(config.getMaxFileSizeAsLong());
      setAllowedFileExtensions(Arrays.asList(config.getAllowedFileExtensions()));
    }};
  }

  public static RevisionHistory RevisionHistory(DatasetRevisionHistory history) {
    return new RevisionHistoryImpl() {{
      setOriginalId(history.getOriginalIdString());
      setRevisions(history.getRevisions()
        .stream()
        .map(OutputTypes::DatasetRevision)
        .toList());
    }};
  }
}
