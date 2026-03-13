package vdi.lane.reconciliation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.veupathdb.lib.s3.s34k.fields.BucketName
import java.util.concurrent.ConcurrentHashMap
import vdi.core.async.WorkerPool
import vdi.core.config.vdi.VDIConfig
import vdi.core.metrics.Metrics
import vdi.core.modules.AbortCB
import vdi.core.modules.AbstractVDIModule

class ReconciliationLane(vdiConfig: VDIConfig, abortCB: AbortCB): AbstractVDIModule(abortCB) {
  private val config = ReconciliationLaneConfig(vdiConfig)

  private lateinit var reconciler: DatasetReconciler

  private val datasetsInProgress = ConcurrentHashMap.newKeySet<String>(config.workerCount.toInt())

  override suspend fun run() {
    val kc = requireKafkaConsumer(config.eventChannel, config.kafkaInConfig)
    val wp = WorkerPool.create<ReconciliationLane>(config.workerCount, config.jobQueueSize) {
      Metrics.ReconciliationHandler.queueSize.inc(it.toDouble())
    }

    reconciler = DatasetReconciler(
      eventRouter    = requireKafkaRouter(config.kafkaOutConfig),
      datasetManager = requireDatasetManager(config.s3Config, BucketName(config.s3Bucket))
    )

    coroutineScope {
      launch(Dispatchers.IO) {
        while (!isShutDown())
          kc.fetchMessages(config.eventMsgKey)
            .map { ReconcilerContext(it, logger) }
            .onEach { it.logger.info("received reconciliation event") }
            .forEach { wp.submit { it.reconcile() } }
      }
    }

    wp.stop()
    kc.close()
    confirmShutdown()
  }

  private fun ReconcilerContext.reconcile() {
    if (datasetsInProgress.add(datasetId)) {
      try {
        reconciler.reconcile(this)
      } finally {
        datasetsInProgress.remove(datasetId)
      }
    } else {
      logger.info("dataset reconciliation already in progress")
    }
  }
}

