package org.veupathdb.vdi.lib.s3.datasets

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.objects.S3Object
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.field.toUserIDOrNull
import org.veupathdb.vdi.lib.s3.datasets.paths.S3DatasetPathFactory
import org.veupathdb.vdi.lib.s3.datasets.paths.S3PathFactory
import org.veupathdb.vdi.lib.s3.datasets.paths.S3Paths
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport
import kotlin.NoSuchElementException

class DatasetManager(private val s3Bucket: S3Bucket) {
  private val pathFactory = S3PathFactory()

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
  fun getDatasetDirectory(ownerID: UserID, datasetID: DatasetID): DatasetDirectory {
    return DatasetDirectoryImpl(ownerID, datasetID, s3Bucket, S3DatasetPathFactory(ownerID, datasetID))
  }

  /**
   * Returns a list of dataset IDs from the target user path.
   *
   * If the user does not have any datasets, the returned list will be empty.
   *
   * @param ownerID WDK user ID of the user who owns or will own the dataset.
   */
  fun listDatasets(ownerID: UserID): List<DatasetID> {
    return s3Bucket.objects.listSubPaths(pathFactory.userDir(ownerID))
      .commonPrefixes()
      .map(::DatasetID)
  }

  /**
   * Returns a list of dataset IDs from the target user path.
   *
   * If the user does not have any datasets, the returned list will be empty.
   *
   * @param ownerID WDK user ID of the user who owns or will own the dataset.
   */
  fun streamAllDatasets(): Stream<DatasetDirectory> {
    val objectIterator = s3Bucket.objects.stream(pathFactory.rootDir()).stream().iterator()
    val datsetDirStream: Iterator<DatasetDirectory> = DatasetDirIterator(objectIterator)
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(datsetDirStream, Spliterator.ORDERED), false)
  }

  fun listUsers(): List<UserID> {
    return s3Bucket.objects.listSubPaths(pathFactory.rootDir())
      .commonPrefixes()
      .map { it.toUserIDOrNull() ?: throw IllegalStateException("invalid user ID: $it") }
  }

  /**
   * An iterator that returns eagerly loaded dataset directories based on a list of sorted S3Objects.
   *
   * All objects must be grouped by dataset for the iterator to function properly.
   */
  private class DatasetDirIterator(
    private val objectStream: Iterator<S3Object>
  ) : Iterator<DatasetDirectory> {
    private var stagedObjects: List<S3Object> = mutableListOf()
    private var currentDataset: DatasetDirectory? = initFirstDataset()

    override fun hasNext(): Boolean {
      return currentDataset != null
    }

    /**
     * Returns the next dataset directory and iterates through objects in originating object stream to prepare subsequent
     * dataset.
     */
    override fun next(): DatasetDirectory {
      val toReturn = currentDataset ?: throw NoSuchElementException()
      prepNext()
      return toReturn
    }

    private fun initFirstDataset(): DatasetDirectory? {
      if (!objectStream.hasNext()) {
        // Stream was empty to start
        return null
      }
      var s3Object = objectStream.next()
      // Get our initial dataset ID, this will be used to detect when we've hit a new dataset.
      val (initialUserID, initialDatasetID) = datasetIdFromS3Object(s3Object)
      stagedObjects = mutableListOf(s3Object)
      while (objectStream.hasNext()) {
        s3Object = objectStream.next()
        val (_, currDatasetID) = datasetIdFromS3Object(s3Object)
        if (initialDatasetID != currDatasetID) {
          // We've found a new dataset, construct our currentDataset with objects seen so far and reset objects variable
          // to contain the new dataset's object.
          currentDataset = EagerlyLoadedDatasetDirectory(
            stagedObjects,
            initialUserID,
            initialDatasetID,
            S3DatasetPathFactory(initialUserID, initialDatasetID)
          )
          // Set staged objects to contain the object belonging to new dataset.
          stagedObjects = mutableListOf(s3Object)
          return currentDataset
        }
        stagedObjects = stagedObjects + s3Object
        if (!objectStream.hasNext()) {
          // Special handling if there's a single directory in the stream.
          currentDataset = EagerlyLoadedDatasetDirectory(
            stagedObjects,
            initialUserID,
            initialDatasetID,
            S3DatasetPathFactory(initialUserID, initialDatasetID)
          )
          // Stream is exhausted. Indicate as much.
          stagedObjects = emptyList()
        }
      }
      return currentDataset
    }

    /**
     * Get the next dataset ready to be returned by Iterator. We must look ahead by one dataset to ensure the one we
     * are vending contains all of its files.
     */
    private fun prepNext(): DatasetDirectory? {
      while (true) {
        if (!objectStream.hasNext()) {
          if (stagedObjects.isNotEmpty()) {
            // Stream is exhausted, construct a dataset out of remaining objects.
            val idsFromObject = datasetIdFromS3Object(stagedObjects.first())
            val pathFactory = S3DatasetPathFactory(idsFromObject.first, idsFromObject.second)
            this.currentDataset = EagerlyLoadedDatasetDirectory(stagedObjects, idsFromObject.first, idsFromObject.second, pathFactory)
            stagedObjects = emptyList()
            return currentDataset
          }
          this.currentDataset = null
          return null
        }
        val (currUserID, currDatasetID) = datasetIdFromS3Object(stagedObjects.first())
        val s3Object = objectStream.next()
        val (_, nextObjectDatasetID) = datasetIdFromS3Object(s3Object)
        // Check if the next object in the stream is in the same dataset as staged objects.
        if (currDatasetID == nextObjectDatasetID) {
          // If so, add to staged objects
          stagedObjects = stagedObjects + s3Object
        } else {
          // Otherwise, create the dataset directory and reset staged objects.
          val pathFactory = S3DatasetPathFactory(currUserID, currDatasetID)
          currentDataset = EagerlyLoadedDatasetDirectory(stagedObjects, currUserID, currDatasetID, pathFactory)
          stagedObjects = mutableListOf(s3Object)
          return currentDataset
        }
      }
    }

    private fun datasetIdFromS3Object(s3Object: S3Object): Pair<UserID, DatasetID> {
      val pathWithoutRoot = s3Object.path.removePrefix(S3Paths.rootDir())
      val pathTokens = pathWithoutRoot.split("/")
      return Pair(UserID(pathTokens[0]), DatasetID(pathTokens[1]))
    }
  }
}