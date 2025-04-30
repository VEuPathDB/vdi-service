package vdi.lane.reconciliation

internal data class SyncIndicator(
  var metaOutOfSync: Boolean,
  var sharesOutOfSync: Boolean,
  var installOutOfSync: Boolean,
) {
  inline val fullyOutOfSync
    get() = metaOutOfSync && sharesOutOfSync && installOutOfSync
}
