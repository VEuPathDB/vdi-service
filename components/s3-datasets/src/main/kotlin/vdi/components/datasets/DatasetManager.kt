package vdi.components.datasets

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import vdi.components.common.DatasetID
import vdi.components.datasets.paths.S3DatasetPathFactory
import vdi.components.datasets.paths.S3PathFactory

class DatasetManager(
  private val rootPrefix: String,
  private val s3Bucket: S3Bucket
) {
  private val pathFactory = S3PathFactory(rootPrefix)

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
  fun getDatasetDirectory(ownerID: Long, datasetID: DatasetID): DatasetDirectory {
    val ownerID = ownerID.toString()
    val datasetID = datasetID.value

    return DatasetDirectoryImpl(ownerID, datasetID, s3Bucket, S3DatasetPathFactory(rootPrefix, ownerID, datasetID))
  }

  /**
   * Returns a list of dataset IDs from the target user path.
   *
   * If the user does not have any datasets, the returned list will be empty.
   *
   * @param ownerID WDK user ID of the user who owns or will own the dataset.
   */
  fun listDatasets(ownerID: Long): List<DatasetID> {
    return s3Bucket.objects.listSubPaths(pathFactory.userDir(ownerID.toString()))
      .commonPrefixes()
      .map(::DatasetID)
  }

  fun listUsers(): List<Long> {
    return s3Bucket.objects.listSubPaths(pathFactory.rootDir())
      .commonPrefixes()
      .map(String::toLong)
  }
}