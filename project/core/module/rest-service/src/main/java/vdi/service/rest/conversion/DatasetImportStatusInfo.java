package vdi.service.rest.conversion;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vdi.core.db.cache.model.DatasetImportStatus;
import vdi.service.rest.generated.model.DatasetImportStatusCode;
import vdi.service.rest.generated.model.DatasetImportStatusInfoImpl;

import java.util.List;
import java.util.Objects;

public class DatasetImportStatusInfo extends DatasetImportStatusInfoImpl {
  public DatasetImportStatusInfo(
    @NotNull DatasetImportStatusCode status,
    @Nullable List<String> messages
  ) {
    setStatus(Objects.requireNonNull(status));
    setMessages(messages);
  }

  public DatasetImportStatusInfo(@NotNull DatasetImportStatusCode status) {
    this(status, null);
  }

  public DatasetImportStatusInfo(@NotNull DatasetImportStatus status, @Nullable List<String> messages) {
    this(EnumTranslator.toExternal(status), messages);
  }
}
