package vdi.lane.reconciliation;

public class SyncIndicator {
  private boolean metaOutOfSync;
  private boolean sharesOutOfSync;
  private boolean installOutOfSync;

  public SyncIndicator() {
  }

  public SyncIndicator(boolean metaOutOfSync, boolean sharesOutOfSync, boolean installOutOfSync) {
    this.metaOutOfSync = metaOutOfSync;
    this.sharesOutOfSync = sharesOutOfSync;
    this.installOutOfSync = installOutOfSync;
  }

  public boolean isMetaOutOfSync() {
    return metaOutOfSync;
  }

  public void setMetaOutOfSync(boolean metaOutOfSync) {
    this.metaOutOfSync = metaOutOfSync;
  }

  public boolean isSharesOutOfSync() {
    return sharesOutOfSync;
  }

  public void setSharesOutOfSync(boolean sharesOutOfSync) {
    this.sharesOutOfSync = sharesOutOfSync;
  }

  public boolean isInstallOutOfSync() {
    return installOutOfSync;
  }

  public void setInstallOutOfSync(boolean installOutOfSync) {
    this.installOutOfSync = installOutOfSync;
  }

  public boolean isFullyOutOfSync() {
    return this.metaOutOfSync && this.sharesOutOfSync && this.installOutOfSync;
  }

  public boolean isOutOfSync() {
    return this.metaOutOfSync || this.sharesOutOfSync || this.installOutOfSync;
  }
}
