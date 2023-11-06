package org.veupathdb.vdi.lib.reconciler

import org.apache.logging.log4j.kotlin.logger
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.kafka.model.triggers.UpdateMetaTrigger
import org.veupathdb.vdi.lib.kafka.router.KafkaRouter
import org.veupathdb.vdi.lib.reconciler.exception.UnsupportedTypeException
import org.veupathdb.vdi.lib.s3.datasets.DatasetDirectory
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager

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
    }
  }

  private fun tryReconcile() {
    logger().info("Beginning reconciliation of ${targetDB.name}")
    targetDB.streamSortedSyncControlRecords().use { targetDBStream ->

      val sourceIterator = datasetManager.streamAllDatasets().iterator()
      val targetIterator = targetDBStream.iterator()

      nextTargetDataset = if (targetIterator.hasNext()) targetIterator.next() else null

      // Iterate through datasets in S3
      while (sourceIterator.hasNext()) {

        // Pop the next DatasetDirectory instance from the S3 stream.
        val sourceDatasetDir: DatasetDirectory = sourceIterator.next()

        logger().info("Checking dataset $sourceDatasetDir for ${targetDB.name}")

        // Target stream is exhausted, everything left in source stream is missing from the target database!
        // Check again if target stream is exhausted, consume source stream if so.
        if (nextTargetDataset == null) {
          consumeEntireSourceStream(sourceIterator, sourceDatasetDir)
          return@use
        }

        // If target dataset stream is "ahead" of source stream, delete
        // the datasets from the target stream until we are aligned
        // again (or the target stream is consumed).
        if (sourceDatasetDir.datasetID.toString() > nextTargetDataset!!.second.datasetID.toString()) {
          while (nextTargetDataset != null && sourceDatasetDir.datasetID.toString() > nextTargetDataset!!.second.datasetID.toString()) {
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
          sendSyncEvent(sourceDatasetDir)
        } else {
          if (isOutOfSync(sourceDatasetDir, nextTargetDataset!!.second)) {
            sendSyncEvent(sourceDatasetDir)
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

  private fun sendSyncEvent(sourceDatasetDir: DatasetDirectory) {
    logger().info("Sending sync event for ${sourceDatasetDir.datasetID}")
    // An update-meta event should trigger synchronization of all dataset components.
    kafkaRouter.sendUpdateMetaTrigger(UpdateMetaTrigger(sourceDatasetDir.ownerID, sourceDatasetDir.datasetID))
  }

  private fun consumeEntireSourceStream(
    sourceIterator: Iterator<DatasetDirectory>,
    sourceDatasetDir: DatasetDirectory
  ) {
    sendSyncEvent(sourceDatasetDir)
    while (sourceIterator.hasNext()) {
      sendSyncEvent(sourceIterator.next())
    }
  }
}