package vdi.lib.reconciler

internal class SyncStatus(
  val metaOutOfSync: Boolean,
  val sharesOutOfSync: Boolean,
  val installOutOfSync: Boolean,
) {
  inline val isOutOfSync get() = metaOutOfSync || sharesOutOfSync || installOutOfSync

  fun toSyncReason(targetDB: ReconcilerTarget) =
    SyncReason.OutOfSync(metaOutOfSync, sharesOutOfSync, installOutOfSync, targetDB)
}
