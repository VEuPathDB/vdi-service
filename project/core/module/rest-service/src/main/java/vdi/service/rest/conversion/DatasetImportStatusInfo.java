package vdi.service.rest.conversion;

import vdi.core.db.cache.model.DatasetImportStatus;
import vdi.service.rest.generated.model.DatasetImportStatusCode;
import vdi.service.rest.generated.model.DatasetImportStatusInfoImpl;

import java.util.List;
import java.util.Objects;

public class DatasetImportStatusInfo extends DatasetImportStatusInfoImpl {
  public DatasetImportStatusInfo(
    DatasetImportStatusCode status,
    List<String> messages
  ) {
    setStatus(Objects.requireNonNull(status));
    setMessages(messages);
  }

  public DatasetImportStatusInfo(DatasetImportStatusCode status) {
    this(status, null);
  }

  public DatasetImportStatusInfo(DatasetImportStatus status, List<String> messages) {
    this(EnumTranslator.toExternal(status), messages);
  }
}
