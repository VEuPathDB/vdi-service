package vdi.service.rest.conversion;

import vdi.core.db.cache.model.DatasetImportStatus;
import vdi.model.DatasetUploadStatus;
import vdi.service.rest.generated.model.DatasetImportStatusCode;
import vdi.service.rest.generated.model.DatasetUploadStatusCode;

public final class EnumTranslator {
  public static DatasetImportStatusCode toExternal(DatasetImportStatus status) {
    return switch (status) {
      case DatasetImportStatus.Complete -> DatasetImportStatusCode.COMPLETE;
      case DatasetImportStatus.Invalid -> DatasetImportStatusCode.INVALID;
      case DatasetImportStatus.InProgress -> DatasetImportStatusCode.INPROGRESS;
      case DatasetImportStatus.Queued -> DatasetImportStatusCode.QUEUED;
      case DatasetImportStatus.Failed -> DatasetImportStatusCode.FAILED;
    };
  }

  public static DatasetUploadStatusCode toExternal(DatasetUploadStatus status) {
    return switch (status) {
      case DatasetUploadStatus.Success -> DatasetUploadStatusCode.COMPLETE;
      case DatasetUploadStatus.Failed  -> DatasetUploadStatusCode.FAILED;
      case DatasetUploadStatus.Running -> DatasetUploadStatusCode.RUNNING;
    };
  }
}
