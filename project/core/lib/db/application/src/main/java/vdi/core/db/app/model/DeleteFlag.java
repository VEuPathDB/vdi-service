package vdi.core.db.app.model;

/**
 * Dataset Deletion Status Indicator
 * <p>
 * This enum represents the different deletion statuses a dataset may be in for
 * install target App databases.
 * <p>
 * Datasets that are marked with {@code NotDeleted} ({@code 0} in the database)
 * are visible to App DB queries for user datasets and may be included in
 * results.
 * <p>
 * Datasets that are marked with {@code DeletedNotUninstalled} ({@code 2} in the
 * database) or {@code DeletedAndUninstalled} ({@code 1} in the database) are
 * not visible to App DB queries.
 * <p>
 * The flow of deletion statuses is:
 * <p>
 * <ol>
 *   <li>{@code NotDeleted} - This is the initial status that is set when a
 *   dataset is first registered in a target App DB.</li>
 *   <li>{@code DeletedNotUninstalled} - This is the status that is set
 *   immediately when a dataset deletion event is fired by the dataset owner.
 *   This status means the dataset should not be visible in App DB queries, but
 *   has not yet had its data purged/uninstalled.</li>
 *   <li>{@code DeletedAndUninstalled} - This status is set after the dataset
 *   data has been successfully purged/uninstalled.  Datasets in this status are
 *   subject to hard deletion at some point in the future.</li>
 * </ol>
 * <p>
 * The integer value representing the statuses does not change in incrementing
 * order as one may expect, it instead moves from {@code 0} to {@code 2} to
 * {@code 1}.  This is to maintain backwards compatibility with previous
 * iterations of the VDI service, where there were only two statuses.  In those
 * VDI versions, a dataset would only be set to the status {@code 1} after a
 * successful uninstallation.  This created an issue, however, where datasets
 * that a user had marked as deleted would still appear in App DB queries if the
 * uninstallation process for that dataset failed.
 */
public enum DeleteFlag {
  NOT_DELETED(0),
  DELETED_NOT_UNINSTALLED(2),
  DELETED_AND_UNINSTALLED(1)
  ;

  public final int value;

  DeleteFlag(int value) {
    this.value = value;
  }

  public static DeleteFlag fromInt(int value) {
    for (var e : values())
      if (e.value == value)
        return e;

    throw new IllegalArgumentException("unrecognized DeleteFlag value: $value");
  }
}
