package vdi.service.rest.conversion;

import vdi.core.db.app.model.InstallStatusDetails;
import vdi.service.rest.generated.model.DatasetInstallStatus;
import vdi.service.rest.generated.model.DatasetInstallStatusDetailsImpl;

import java.util.List;
import java.util.Objects;

public class DatasetInstallStatusDetails extends DatasetInstallStatusDetailsImpl {
  public DatasetInstallStatusDetails(
    DatasetInstallStatus status,
    List<String> messages
  ) {
    setStatus(Objects.requireNonNull(status));
    setMessages(messages);
  }

  public DatasetInstallStatusDetails(InstallStatusDetails internal) {
    this(EnumTranslator.toExternal(internal.getStatus()), internal.getMessages());
  }
}
