package vdi.service.rest.conversion;

import vdi.core.db.cache.model.BrokenImportRecord;
import vdi.core.plugin.registry.PluginRegistry;
import vdi.model.meta.DatasetType;
import vdi.service.rest.generated.model.BrokenImportDetails;
import vdi.service.rest.generated.model.BrokenImportDetailsImpl;
import vdi.service.rest.generated.model.DatasetTypeOutput;
import vdi.service.rest.generated.model.DatasetTypeOutputImpl;

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

  public static DatasetTypeOutput DatasetTypeOutput(DatasetType type) {
    return new DatasetTypeOutputImpl() {{
      setName(type.getNameString());
      setVersion(type.getVersion());
      setCategory(PluginRegistry.require(type).getCategory());
    }};
  }
}
