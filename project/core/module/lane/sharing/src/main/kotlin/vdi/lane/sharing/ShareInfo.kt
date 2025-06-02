package vdi.lane.sharing

internal data class ShareInfo(val offer: ShareState, val receipt: ShareState) {
  inline val visibleInTarget
    get() = offer == ShareState.Yes && receipt == ShareState.Yes
}
