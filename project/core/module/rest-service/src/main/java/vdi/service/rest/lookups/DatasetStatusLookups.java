package vdi.service.rest.lookups;

import vdi.model.DatasetUploadStatus;
import vdi.model.meta.UserID;
import vdi.service.rest.s3.DatasetStore;

public final class DatasetStatusLookups {
  public static DatasetUploadStatus calcUploadStatus(UserID userId, String datasetId) {
    var dir = DatasetStore.getDatasetDirectory(userId, datasetId);

    if (dir.hasUploadFile())
      return DatasetUploadStatus.Success;

    if (dir.hasUploadErrorFile())
      return DatasetUploadStatus.Failed;

    return DatasetUploadStatus.Running;
  }

  public static String getUploadError(UserID userId, String datasetId) {
    var errors = DatasetStore.getDatasetDirectory(userId, datasetId)
      .getUploadErrorFile()
      .load();

    return errors == null
      ? null
      : errors.getMessage();
  }
}
