package vdi.lane.reconciliation

internal enum class ReimportIndicator {
  /**
   * Reimporting the target dataset is not possible due to the data being
   * invalid or the state in MinIO being broken (missing import-ready.zip).
   */
  ReimportNotPossible,

  /**
   * Reimport needed when:
   * * A previous import attempt failed, but the cache-db does not have a
   *   message recorded.  In this case we re-run to provide an error message
   *   for the user.
   * * The import is not marked as failed, but we do not have both an
   *   install-ready.zip file and vdi-manifest.json file
   */
  NeedReimport,

  /**
   * We already have an import-ready.zip and vdi-manifest.json or the import
   * previously failed, and we have an error message for the user.
   */
  ReimportNotNeeded,
}
