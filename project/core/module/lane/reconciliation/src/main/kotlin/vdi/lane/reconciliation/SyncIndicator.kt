package vdi.lane.reconciliation

internal data class SyncIndicator(
  /**
   * Indicates whether any installable meta files are out of sync with an
   * install target.
   *
   * Meta files include the VDI dataset metadata JSON, or any data dictionary
   * mapping files.
   */
  var metaOutOfSync: Boolean,

  /**
   * Indicates whether any share information files are out of sync with an
   * install target.
   */
  var sharesOutOfSync: Boolean,

  /**
   * Indicates whether a dataset's installable data is out of sync with an
   * install target.
   */
  var installOutOfSync: Boolean,
) {
  inline val fullyOutOfSync
    get() = metaOutOfSync && sharesOutOfSync && installOutOfSync

  inline val outOfSync
    get() = metaOutOfSync || sharesOutOfSync || installOutOfSync
}
