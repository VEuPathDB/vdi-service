package org.veupathdb.vdi.lib.reconciler

import org.apache.logging.log4j.kotlin.logger
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.kafka.model.triggers.UpdateMetaTrigger
import org.veupathdb.vdi.lib.kafka.router.KafkaRouter
import org.veupathdb.vdi.lib.s3.datasets.DatasetDirectory
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager
import org.veupathdb.vdi.lib.s3.datasets.exception.MalformedDatasetException
import vdi.component.metrics.Metrics

/**
 * Component for synchronizing the dataset object store (the source of truth for datasets) with a target database.
 *
 * This includes both our internal "cache DB", used to answer questions about user dataset metadata/status and
 * our application databases in which the contents of datasets are installed.
 */
class ReconcilerInstance(
  private val targetDB: ReconcilerTarget,
  private val datasetManager: DatasetManager,
  private val kafkaRouter: KafkaRouter
) {
  private var nextTargetDataset: Pair<VDIDatasetType, VDISyncControlRecord>? = null

  val name = targetDB.name

  fun reconcile() {
    try {
      tryReconcile()
    } catch (e: Exception) {
      // Don't re-throw error, ensure exception is logged and soldier on for future reconciliation.
      logger().error("Failure running reconciler for " + targetDB.name, e)
      Metrics.malformedDatasetFound.labels("target_name", targetDB.name).inc()
    }
  }

  private fun tryReconcile() {
    logger().info("Beginning reconciliation of ${targetDB.name}")
    targetDB.streamSortedSyncControlRecords().use { targetDBStream ->

      val sourceIterator = datasetManager.streamAllDatasets().iterator()
      val targetIterator = targetDBStream.iterator()

      nextTargetDataset = if (targetIterator.hasNext()) targetIterator.next() else null

      // Iterate through datasets in S3.
      while (sourceIterator.hasNext()) {

        // Try to read a dataset from S3.
        var sourceDatasetDir: DatasetDirectory
        try {
          // Pop the next DatasetDirectory instance from the S3 stream.
          sourceDatasetDir = sourceIterator.next()
        } catch (e: MalformedDatasetException) {
          // Skip the dataset if it's malformed for some reason. As things settle down, we may want to clean it up in MinIO?
          logger().error("Found a malformed dataset in S3. Skipping dataset and continuing on.", e)
          Metrics.malformedDatasetFound.labels("target_name", targetDB.name).inc()
          continue
        }

        logger().info("Checking dataset ${sourceDatasetDir.ownerID}/${sourceDatasetDir.datasetID} for ${targetDB.name}")

        // Target stream is exhausted, everything left in source stream is missing from the target database!
        // Check again if target stream is exhausted, consume source stream if so.
        if (nextTargetDataset == null) {
          consumeEntireSourceStream(sourceIterator, sourceDatasetDir)
          return@use
        }

        // If target dataset stream is "ahead" of source stream, delete
        // the datasets from the target stream until we are aligned
        // again (or the target stream is consumed).
        if (sourceDatasetDir.datasetID.toString().compareTo(nextTargetDataset!!.second.datasetID.toString(), true) > 0) {

          // Delete datasets until and advance target iterator until streams are aligned.
          while (nextTargetDataset != null && sourceDatasetDir.datasetID.toString().compareTo(nextTargetDataset!!.second.datasetID.toString(), true) > 0) {
            logger().info("Attempting to delete dataset with owner ${sourceDatasetDir.ownerID} and ID ${sourceDatasetDir.datasetID} " +
                    "because ${nextTargetDataset!!.second.datasetID} is lexigraphically greater than our ID.")
            tryDeleteDataset(targetDB, nextTargetDataset!!.first, nextTargetDataset!!.second.datasetID)
            nextTargetDataset = if (targetIterator.hasNext()) targetIterator.next() else null
          }
        }

        // Check again if target stream is exhausted, consume source stream if so.
        if (nextTargetDataset == null) {
          consumeEntireSourceStream(sourceIterator, sourceDatasetDir)
          return@use
        }

        if (sourceDatasetDir.datasetID.toString() < nextTargetDataset!!.second.datasetID.toString()) {
          // Dataset is in source, but not in target. Send an event.
          sendSyncIfRelevant(sourceDatasetDir)
        } else {
          // Dataset is in source and target. Check dates to see if sync is needed.
          if (isOutOfSync(sourceDatasetDir, nextTargetDataset!!.second)) {
            sendSyncIfRelevant(sourceDatasetDir)
          }
          // Advance next target dataset pointer, we're done with this one since it's in sync.
          nextTargetDataset = if (targetIterator.hasNext()) targetIterator.next() else null
        }
      }

      // Consume target stream, deleting all remaining datasets.
      while (targetIterator.hasNext()) {
        val targetDatasetControl = targetIterator.next()
        logger().info("Attempting to delete " + targetDatasetControl.second.datasetID)
        tryDeleteDataset(
          targetDB,
          datasetType = targetDatasetControl.first,
          datasetID = targetDatasetControl.second.datasetID
        )
      }
      logger().info("Completed reconciliation")
    }
  }

  private fun tryDeleteDataset(targetDB: ReconcilerTarget, datasetType: VDIDatasetType, datasetID: DatasetID) {
    try {
      logger().info("Trying to delete dataset $datasetID.")
      Metrics.reconcilerDatasetDeleted.labels("target_name", targetDB.name).inc()
      targetDB.deleteDataset(datasetID = datasetID, datasetType = datasetType)
    } catch (e: Exception) {
      // Swallow exception and alert if unable to delete. Reconciler can safely recover, but the dataset
      // may need a manual inspection.
      logger().error("Failed to delete dataset $datasetID of type $datasetType from db ${targetDB.name}", e)
    }
  }

  /**
   * Returns true if any of our scopes are out of sync.
   */
  private fun isOutOfSync(ds: DatasetDirectory, targetLastUpdated: VDISyncControlRecord): Boolean {
    val shareOos = targetLastUpdated.sharesUpdated.isBefore(ds.getLatestShareTimestamp(targetLastUpdated.sharesUpdated))
    val dataOos = targetLastUpdated.dataUpdated.isBefore(ds.getLatestDataTimestamp(targetLastUpdated.dataUpdated))
    val metaOos = targetLastUpdated.metaUpdated.isBefore(ds.getMeta().lastModified())
    return shareOos || dataOos || metaOos
  }

  private fun sendSyncIfRelevant(sourceDatasetDir: DatasetDirectory) {
    if (targetDB.type == ReconcilerTargetType.Install) {
      val relevantProjects = sourceDatasetDir.getMeta().load()!!.projects
      if (!relevantProjects.contains(targetDB.name)) {
        logger().info("Skipping dataset ${sourceDatasetDir.ownerID}/${sourceDatasetDir.datasetID} as it does not target ${targetDB.name}")
        return
      }
    }

    sendSyncEvent(sourceDatasetDir)
  }

  private fun sendSyncEvent(sourceDatasetDir: DatasetDirectory) {
    logger().info("Sending sync event for ${sourceDatasetDir.datasetID}")
    // An update-meta event should trigger synchronization of all dataset components.
    kafkaRouter.sendUpdateMetaTrigger(UpdateMetaTrigger(sourceDatasetDir.ownerID, sourceDatasetDir.datasetID))
    Metrics.reconcilerDatasetSynced.labels("target_name", targetDB.name).inc()
  }

  private fun consumeEntireSourceStream(
    sourceIterator: Iterator<DatasetDirectory>,
    sourceDatasetDir: DatasetDirectory
  ) {
    sendSyncIfRelevant(sourceDatasetDir)
    while (sourceIterator.hasNext()) {
      sendSyncIfRelevant(sourceIterator.next())
    }
  }
}