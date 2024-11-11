package vdi.component.s3

import org.slf4j.LoggerFactory
import org.veupathdb.lib.s3.s34k.objects.S3Object
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.component.metrics.Metrics
import vdi.component.s3.exception.MalformedDatasetException
import vdi.component.s3.paths.S3DatasetPathFactory

/**
 * An iterator that returns eagerly loaded dataset directories based on a list
 * of sorted S3Objects.
 *
 * All objects must be grouped by dataset for the iterator to function properly.
 */
internal class DatasetDirIterator(private val objectStream: Iterator<S3Object>) : Iterator<DatasetDirectory> {
  private val log = LoggerFactory.getLogger(javaClass)

  private val stagedObjects: MutableList<S3Object> = mutableListOf()

  private var currentDataset: DatasetDirectory? = initFirstDataset()

  override fun hasNext() = currentDataset != null

  /**
   * Returns the next dataset directory and iterates through objects in
   * originating object stream to prepare subsequent dataset.
   */
  override fun next() = (currentDataset ?: throw NoSuchElementException())
    .also { currentDataset = prepNext() }

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

  private fun datasetIdFromS3Object(s3Object: S3Object) =
    s3Object.path.split("/").let { (userID, datasetID) -> UserID(userID) to DatasetID(datasetID) }
}
