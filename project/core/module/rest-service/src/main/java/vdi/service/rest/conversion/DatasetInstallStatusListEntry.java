package vdi.service.rest.conversion;

import vdi.core.db.app.model.InstallStatuses;
import vdi.service.rest.generated.model.DatasetInstallStatusListEntryImpl;

import java.util.Objects;
import java.util.Optional;

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
    var opt = Optional.ofNullable(internal);
    this(
      installTarget,
      opt.map(InstallStatuses::getMeta)
        .map(DatasetInstallStatusDetails::new)
        .orElse(null),
      opt.map(InstallStatuses::getData)
        .map(DatasetInstallStatusDetails::new)
        .orElse(null)
    );
  }
}
