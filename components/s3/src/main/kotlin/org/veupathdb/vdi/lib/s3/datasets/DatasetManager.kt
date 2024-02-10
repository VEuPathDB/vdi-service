package org.veupathdb.vdi.lib.s3.datasets

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import java.util.stream.Stream

interface DatasetManager {

  /**
   * Returns a [DatasetDirectory] instance representing the S3 "directory" for
   * the target dataset.
   *
   * This method will always return an instance regardless of whether such a
   * path actually exists in S3.  The existence of the path may be tested using
   * the [DatasetDirectory.exists] method.
   *
   * @param ownerID WDK user ID of the user who owns or will the dataset.
   *
   * @param datasetID ID of the target dataset.
   *
   * @return A new [DatasetDirectory] instance representing the S3 path for the
   * target dataset.
   */
  fun getDatasetDirectory(ownerID: UserID, datasetID: DatasetID): DatasetDirectory

  /**
   * Returns a list of dataset IDs from the target user path.
   *
   * If the user does not have any datasets, the returned list will be empty.
   *
   * @param ownerID WDK user ID of the user who owns or will own the dataset.
   */
  fun listDatasets(ownerID: UserID): List<DatasetID>

  fun streamAllDatasets(): Stream<DatasetDirectory>

  fun listUsers(): List<UserID>
}