package vdi.service.rest.lookups;

import vdi.model.DatasetUploadStatus;
import vdi.model.meta.DatasetID;
import vdi.model.meta.UserID;
import vdi.service.rest.s3.DatasetStore;

public final class DatasetStatusLookups {
  public static DatasetUploadStatus calcUploadStatus(UserID userId, DatasetID datasetId) {
    DatasetStore.getDatasetDirectory(userId, datasetId);
  }
}
