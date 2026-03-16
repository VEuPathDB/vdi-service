package vdi.core.pruner;

import org.slf4j.LoggerFactory;
import vdi.core.db.cache.model.DeletedDataset;
import vdi.logging.LoggingExt;
import vdi.logging.MarkedLogger;
import vdi.model.meta.DatasetType;
import vdi.model.meta.UserID;

import java.util.List;

public class DeletionContext {
  private final DeletedDataset dataset;

  private final MarkedLogger logger;

  private PrunableState state;

  public DeletionContext(DeletedDataset dataset) {
    this.dataset = dataset;
    this.logger = new MarkedLogger(
      new String[]{
        LoggingExt.markString(LoggingExt.PrefixUserID, dataset.getOwnerID()),
        LoggingExt.markString(LoggingExt.PrefixDatasetID, dataset.getDatasetIdString()),
      },
      LoggerFactory.getLogger(getClass())
    );
  }

  public DeletedDataset getDataset() {
    return this.dataset;
  }

  public MarkedLogger getLogger() {
    return this.logger;
  }

  public PrunableState getState() {
    return this.state;
  }

  public void setState(PrunableState state) {
    this.state = state;
  }

  public UserID getOwnerId() {
    return this.dataset.getOwnerID();
  }

  public String getDatasetId() {
    return this.dataset.getDatasetIdString();
  }

  public List<String> getProjects() {
    return this.dataset.getProjects();
  }

  public DatasetType getDataType() {
    return this.dataset.getDataType();
  }
}
