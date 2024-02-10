package org.veupathdb.vdi.lib.s3.datasets

import org.slf4j.LoggerFactory
import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.objects.S3Object
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.field.toUserIDOrNull
import org.veupathdb.vdi.lib.s3.datasets.exception.MalformedDatasetException
import org.veupathdb.vdi.lib.s3.datasets.paths.S3DatasetPathFactory
import org.veupathdb.vdi.lib.s3.datasets.paths.S3Paths
import vdi.component.metrics.Metrics
import java.util.Spliterators
import java.util.Spliterator
import java.util.stream.Stream
import java.util.stream.StreamSupport

fun DatasetManager(s3Bucket: S3Bucket): DatasetManager = DatasetManagerImpl(s3Bucket)

internal class DatasetManagerImpl(private val s3Bucket: S3Bucket) : DatasetManager {

  override fun getDatasetDirectory(ownerID: UserID, datasetID: DatasetID): DatasetDirectory =
    DatasetDirectoryImpl(ownerID, datasetID, s3Bucket, S3DatasetPathFactory(ownerID, datasetID))

  override fun listDatasets(ownerID: UserID): List<DatasetID> =
    s3Bucket.objects.listSubPaths(S3Paths.userDir(ownerID))
      .commonPrefixes()
      .map(::DatasetID)

  override fun streamAllDatasets(): Stream<DatasetDirectory> =
    StreamSupport.stream(Spliterators.spliteratorUnknownSize(
      DatasetDirIterator(s3Bucket.objects.stream().stream().iterator()),
      Spliterator.ORDERED
    ), false)

  override fun listUsers(): List<UserID> =
    s3Bucket.objects.listSubPaths()
      .commonPrefixes()
      .map { it.toUserIDOrNull() ?: throw IllegalStateException("invalid user ID: $it") }
}

/**
 * An iterator that returns eagerly loaded dataset directories based on a list of sorted S3Objects.
 *
 * All objects must be grouped by dataset for the iterator to function properly.
 */
private class DatasetDirIterator(private val objectStream: Iterator<S3Object>) : Iterator<DatasetDirectory> {
  private val log = LoggerFactory.getLogger(javaClass)
  private val stagedObjects: MutableList<S3Object> = mutableListOf()
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
    currentDataset = prepNext()
    return toReturn
  }

  private fun initFirstDataset(): DatasetDirectory? {
    if (!objectStream.hasNext()) {
      // Stream was empty to start
      return null
    }

    stagedObjects.add(objectStream.next())

    return prepNext()
  }

  /**
   * Get the next dataset ready to be returned by Iterator. We must look ahead
   * by one dataset to ensure the one we are vending contains all of its
   * files.
   */
  private fun prepNext(): DatasetDirectory? {
    while (true) {

      // If the source object stream has been exhausted
      if (!objectStream.hasNext()) {

        // and we have staged objects
        if (stagedObjects.isNotEmpty()) {

          // Construct a dataset out of remaining staged objects.
          val (userID, datasetID) = datasetIdFromS3Object(stagedObjects.first())
          val pathFactory = S3DatasetPathFactory(userID, datasetID)

          try {
            return EagerlyLoadedDatasetDirectory(stagedObjects, userID, datasetID, pathFactory)
          } catch (e: MalformedDatasetException) {
            // Dataset is malformed, note it and return null since our stream is exhausted.
            Metrics.malformedDatasetFound.inc()
            log.warn("found malformed dataset $userID/$datasetID", e)
            return null
          } finally {
            stagedObjects.clear()
          }
        }

        // Nothing staged and stream is exhausted, we're done!
        return null
      }

      // Get the userID and datasetID of the staged objects.
      val (currUserID, currDatasetID) = datasetIdFromS3Object(stagedObjects.first())

      // Get the next object and the userID/datasetID of the next object in the stream.
      val s3Object = objectStream.next()
      val (_, nextObjectDatasetID) = datasetIdFromS3Object(s3Object)

      // Check if the next object in the stream is in the same dataset as staged objects.
      if (currDatasetID == nextObjectDatasetID) {
        // If so, add to staged objects.
        stagedObjects.add(s3Object)
      } else {
        try {
          // Otherwise, create the dataset directory and reset staged objects.
          val pathFactory = S3DatasetPathFactory(currUserID, currDatasetID)
          return EagerlyLoadedDatasetDirectory(stagedObjects, currUserID, currDatasetID, pathFactory)
        } catch (e: MalformedDatasetException) {
          // Dataset is malformed. Note it, and set staged objects to the next object in stream to start accumulating
          // objects for the next dataset.
          Metrics.malformedDatasetFound.inc()
          log.warn("found malformed dataset $currUserID/$currDatasetID", e)
          continue
        } finally {
          stagedObjects.clear()
          stagedObjects.add(s3Object)
        }
      }
    }
  }

  private fun datasetIdFromS3Object(s3Object: S3Object): Pair<UserID, DatasetID> {
    val pathTokens = s3Object.path.split("/")
    return Pair(UserID(pathTokens[0]), DatasetID(pathTokens[1]))
  }
}
