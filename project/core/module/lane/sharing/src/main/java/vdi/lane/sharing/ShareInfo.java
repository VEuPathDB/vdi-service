package vdi.lane.sharing;

public record ShareInfo(ShareState offer, ShareState receipt) {
  public boolean isVisibleInTarget() {
    return offer == ShareState.YES && receipt == ShareState.YES;
  }
}
