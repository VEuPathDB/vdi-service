package org.veupathdb.vdi.lib.reconciler

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.CloseableThreadContext
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.kafka.model.triggers.UpdateMetaTrigger
import org.veupathdb.vdi.lib.kafka.router.KafkaRouter
import org.veupathdb.vdi.lib.s3.datasets.DatasetDirectory
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager
import java.util.*

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
    private val log = LoggerFactory.getLogger(javaClass)

    private var nextTargetDataset: VDISyncControlRecord? = null

    fun reconcile() {
        try {
            MDC.put("traceId", targetDB.name + "-" + UUID.randomUUID().toString())
            tryReconcile()
        } catch (e: Exception) {
            log.error("Failure running reconciler for " + targetDB.name, e)
        } finally {
            MDC.remove("traceId")
        }
    }

    private fun tryReconcile() {
        log.info("Beginning reconciliation of " + targetDB.name)
        targetDB.streamSortedSyncControlRecords().use { targetDBStream ->
            val sourceIterator = datasetManager.streamAllDatasets().iterator()
            val targetIterator = targetDBStream.iterator()
            nextTargetDataset = if (targetIterator.hasNext()) targetIterator.next() else null
            while (sourceIterator.hasNext()) {
                val sourceDatasetDir = sourceIterator.next()
                log.info("Checking dataset {} for {}", sourceDatasetDir, targetDB.name)

                // Target stream is exhausted, everything left in source stream is missing!
                // Check again if target stream is exhausted, consume source stream if so.
                if (nextTargetDataset == null) {
                    consumeEntireSourceStream(sourceIterator, sourceDatasetDir)
                    return@use
                }

                // If target dataset stream is "ahead" of source stream, delete from stream
                if (sourceDatasetDir.datasetID.toString() > nextTargetDataset!!.datasetID.toString()) {
                    while (nextTargetDataset != null && sourceDatasetDir.datasetID.toString() > nextTargetDataset!!.datasetID.toString()) {
                        // TODO: Are we ok with the reconciler entirely deleting the dataset as soon as detected?
                        // Should we have a grace period?
                        targetDB.deleteDataset(nextTargetDataset!!.datasetID)
                        nextTargetDataset = if (targetIterator.hasNext()) targetIterator.next() else null
                    }
                }

                // Check again if target stream is exhausted, consume source stream if so.
                if (nextTargetDataset == null) {
                    consumeEntireSourceStream(sourceIterator, sourceDatasetDir)
                    return@use
                }

                if (sourceDatasetDir.datasetID.toString() < nextTargetDataset!!.datasetID.toString()) {
                    // Dataset is in source, but not in target. Send an event.
                    sendSyncEvent(sourceDatasetDir) // Do we need a way to specify out of sync targets?
                } else {
                    if (isOutOfSync(sourceDatasetDir, nextTargetDataset!!)) {
                        sendSyncEvent(sourceDatasetDir)
                    }
                    // Advance next target dataset pointer, we're done with this one since it's in sync.
                    nextTargetDataset = if (targetIterator.hasNext()) targetIterator.next() else null
                }
            }
            // Consume target stream, deleting all remaining datasets.
            while (targetIterator.hasNext()) {
                val targetDatasetControl = targetIterator.next()
                log.info("Would delete " + targetDatasetControl.datasetID)
                targetDB.deleteDataset(datasetID = targetDatasetControl.datasetID)
            }
        }
    }

    /**
     * Returns true if any of our scopes are out of sync.
     */
    private fun isOutOfSync(ds: DatasetDirectory, targetLastUpdated: VDISyncControlRecord): Boolean {
        log.info("Target share status {} source share status {}, {}", targetLastUpdated.sharesUpdated, ds.getLatestShareTimestamp(targetLastUpdated.sharesUpdated), targetDB.name)
        log.info("Target data status {} source data status {}, {}", targetLastUpdated.dataUpdated, ds.getLatestDataTimestamp(targetLastUpdated.dataUpdated), targetDB.name)
        log.info("Target meta status {} source meta status {}, {}", targetLastUpdated.metaUpdated, ds.getMeta().lastModified(), targetDB.name)

        val shareOos = targetLastUpdated.sharesUpdated.isBefore(ds.getLatestShareTimestamp(targetLastUpdated.sharesUpdated))
        val dataOos = targetLastUpdated.dataUpdated.isBefore(ds.getLatestDataTimestamp(targetLastUpdated.dataUpdated))
        val metaOos = targetLastUpdated.metaUpdated.isBefore(ds.getMeta().lastModified())
        return shareOos || dataOos || metaOos
    }

    private fun sendSyncEvent(sourceDatasetDir: DatasetDirectory) {
        log.info("Sending sync event for {}", sourceDatasetDir.datasetID)
        // An update-meta event should trigger synchronization of all dataset components.
        kafkaRouter.sendUpdateMetaTrigger(UpdateMetaTrigger(sourceDatasetDir.ownerID, sourceDatasetDir.datasetID))
    }

    private fun consumeEntireSourceStream(sourceIterator: Iterator<DatasetDirectory>, sourceDatasetDir: DatasetDirectory) {
        sendSyncEvent(sourceDatasetDir)
        while (sourceIterator.hasNext()) {
            sendSyncEvent(sourceIterator.next())
        }
        return
    }
}