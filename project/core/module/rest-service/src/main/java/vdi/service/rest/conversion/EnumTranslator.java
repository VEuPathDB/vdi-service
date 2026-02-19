package vdi.service.rest.conversion;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import vdi.core.db.app.model.InstallStatus;
import vdi.core.db.cache.model.DatasetImportStatus;
import vdi.model.DatasetUploadStatus;
import vdi.service.rest.generated.model.DatasetImportStatusCode;
import vdi.service.rest.generated.model.DatasetInstallStatus;
import vdi.service.rest.generated.model.DatasetUploadStatusCode;

public final class EnumTranslator {
  @NotNull
  @Contract(pure = true)
  public static DatasetImportStatusCode toExternal(@NotNull DatasetImportStatus status) {
    return switch (status) {
      case DatasetImportStatus.Complete -> DatasetImportStatusCode.COMPLETE;
      case DatasetImportStatus.Invalid -> DatasetImportStatusCode.INVALID;
      case DatasetImportStatus.InProgress -> DatasetImportStatusCode.INPROGRESS;
      case DatasetImportStatus.Queued -> DatasetImportStatusCode.QUEUED;
      case DatasetImportStatus.Failed -> DatasetImportStatusCode.FAILED;
    };
  }

  @NotNull
  @Contract(pure = true)
  public static DatasetUploadStatusCode toExternal(@NotNull DatasetUploadStatus status) {
    return switch (status) {
      case DatasetUploadStatus.Success -> DatasetUploadStatusCode.SUCCESS;
      case DatasetUploadStatus.Rejected -> DatasetUploadStatusCode.REJECTED;
      case DatasetUploadStatus.Failed -> DatasetUploadStatusCode.FAILED;
      case DatasetUploadStatus.Running -> DatasetUploadStatusCode.RUNNING;
    };
  }

  @NotNull
  @Contract(pure = true)
  public static DatasetInstallStatus toExternal(@NotNull InstallStatus status) {
    return switch (status) {
      case Complete -> DatasetInstallStatus.COMPLETE;
      case FailedInstallation -> DatasetInstallStatus.FAILEDINSTALLATION;
      case FailedValidation -> DatasetInstallStatus.FAILEDVALIDATION;
      case MissingDependency -> DatasetInstallStatus.MISSINGDEPENDENCY;
      case Running -> DatasetInstallStatus.RUNNING;
      case ReadyForReinstall -> DatasetInstallStatus.READYFORREINSTALL;
    };
  }
}
