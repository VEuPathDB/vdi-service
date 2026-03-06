package vdi.service.rest.conversion;

import vdi.core.db.cache.model.BrokenImportRecord;
import vdi.service.rest.generated.model.BrokenImportDetailsImpl;

public class BrokenImportDetails extends BrokenImportDetailsImpl {
  public BrokenImportDetails(BrokenImportRecord record) {
    setDatasetId(record.getDatasetIdAsString());
    setOwner(record.getOwnerID().toLong());
    setDatasetType(new DatasetTypeOutput(record.getType()));
    setInstallTargets(record.getInstallTargets());
    setMessages(record.getMessages());
  }
}
