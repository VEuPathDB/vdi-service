package vdi.service.rest.conversion;

import vdi.model.DatasetUploadStatus;
import vdi.service.rest.generated.model.DatasetUploadStatusCode;
import vdi.service.rest.generated.model.DatasetUploadStatusInfoImpl;

import java.util.Objects;

public class DatasetUploadStatusInfo extends DatasetUploadStatusInfoImpl {
  public DatasetUploadStatusInfo(
    DatasetUploadStatusCode status,
    String message
  ) {
    setStatus(Objects.requireNonNull(status));
    setMessage(message);
  }

  public DatasetUploadStatusInfo(DatasetUploadStatusCode status) {
    this(status, null);
  }

  public DatasetUploadStatusInfo(DatasetUploadStatus status, String message) {
    this(EnumTranslator.toExternal(status), message);
  }
}
