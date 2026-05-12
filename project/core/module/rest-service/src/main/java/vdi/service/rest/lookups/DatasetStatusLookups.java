package vdi.service.rest.lookups;

import vdi.model.meta.UserID;
import vdi.service.rest.s3.DatasetStore;

public final class DatasetStatusLookups {
  public static String getUploadError(UserID userId, String datasetId) {
    var errors = DatasetStore.getDatasetDirectory(userId, datasetId)
      .getUploadErrorFile()
      .load();

    return errors == null
      ? null
      : errors.getMessage();
  }
}
