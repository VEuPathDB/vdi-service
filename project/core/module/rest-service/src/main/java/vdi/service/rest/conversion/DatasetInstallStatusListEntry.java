package vdi.service.rest.conversion;

import vdi.core.db.app.model.InstallStatusDetails;
import vdi.core.db.app.model.InstallStatuses;
import vdi.service.rest.generated.model.DatasetInstallStatusListEntryImpl;

import java.util.Map;
import java.util.Objects;

public class DatasetInstallStatusListEntry extends DatasetInstallStatusListEntryImpl {
  public DatasetInstallStatusListEntry(
    String installTarget,
    DatasetInstallStatusDetails meta,
    DatasetInstallStatusDetails data
  ) {
    setInstallTarget(Objects.requireNonNull(installTarget));
    setMeta(meta);
    setData(data);
  }

  public DatasetInstallStatusListEntry(String installTarget, InstallStatuses internal) {
    this(
      installTarget,
      internal == null ? null : wrapInternal(internal.getMeta()),
      internal == null ? null : wrapInternal(internal.getData())
    );
  }

  public DatasetInstallStatusListEntry(Map.Entry<String, InstallStatuses> entry) {
    this(entry.getKey(), entry.getValue());
  }

  private static DatasetInstallStatusDetails wrapInternal(InstallStatusDetails details) {
    return details == null ? null : new DatasetInstallStatusDetails(details);
  }
}
