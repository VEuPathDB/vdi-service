package vdi.lib.db.app.model

/**
 * Dataset Deletion Status Indicator
 *
 * This enum represents the different deletion statuses a dataset may be in for
 * install target App databases.
 *
 * Datasets that are marked with [NotDeleted] (`0` in the database) are visible
 * to App DB queries for user datasets and may be included in results.
 *
 * Datasets that are marked with [DeletedNotUninstalled] (`2` in the database)
 * or [DeletedAndUninstalled] (`3` in the database) are not visible to App DB
 * queries.
 *
 * The flow of deletion statuses is:
 *
 * 1. [NotDeleted] - This is the initial status that is set when a dataset is
 *    first registered in a target App DB.
 * 2. [DeletedNotUninstalled] - This is the status that is set immediately when
 *    a dataset deletion event is fired by the dataset owner.  This status means
 *    the dataset should not be visible in App DB queries, but has not yet had
 *    its data purged/uninstalled.
 * 3. [DeletedAndUninstalled] - This status is set after the dataset data has
 *    been successfully purged/uninstalled.  Datasets in this status are subject
 *    to hard deletion at some point in the future.
 *
 * The integer value representing the statuses does not change in incrementing
 * order as one may expect, it instead moves from `0` to `2` to `1`.  This is to
 * maintain backwards compatibility with previous iterations of the VDI service,
 * where there were only two statuses.  In those VDI versions, a dataset would
 * only be set to the status `1` after a successful uninstall.  This created an
 * issue, however, where datasets that a user had marked as deleted would still
 * appear in App DB queries if the uninstall process for that dataset failed.
 */
enum class DeleteFlag(val value: Int) {
  NotDeleted(0),
  DeletedNotUninstalled(2),
  DeletedAndUninstalled(1),
  ;

  companion object {
    @JvmStatic
    fun fromInt(value: Int): DeleteFlag {
      for (enumValue in entries)
        if (value == enumValue.value)
          return enumValue

      throw IllegalArgumentException("unrecognized DeleteFlag value: $value")
    }
  }
}
