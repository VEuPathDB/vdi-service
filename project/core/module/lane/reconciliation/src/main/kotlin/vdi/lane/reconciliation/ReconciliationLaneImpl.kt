package vdi.lane.reconciliation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import vdi.model.data.DatasetID
import vdi.model.data.UserID
import java.util.concurrent.ConcurrentHashMap
import vdi.core.async.WorkerPool
import vdi.core.kafka.EventSource
import vdi.logging.logger
import vdi.core.metrics.Metrics
import vdi.core.modules.AbortCB
import vdi.core.modules.AbstractVDIModule

internal class ReconciliationLaneImpl(
  private val config: ReconciliationLaneConfig,
  abortCB: AbortCB
)
  : ReconciliationLane
  , AbstractVDIModule(abortCB, logger<ReconciliationLane>())
{
  private lateinit var reconciler: DatasetReconciler

  private val datasetsInProgress = ConcurrentHashMap.newKeySet<DatasetID>(32)

  override suspend fun run() {
    val kc = requireKafkaConsumer(config.eventChannel, config.kafkaInConfig)
    val wp = WorkerPool.create<ReconciliationLane>(config.workerCount, config.jobQueueSize) {
      Metrics.ReconciliationHandler.queueSize.inc(it.toDouble())
    }

    reconciler = DatasetReconciler(
      eventRouter = requireKafkaRouter(config.kafkaOutConfig),
      datasetManager = requireDatasetManager(config.s3Config, config.s3Bucket)
    )

    coroutineScope {
      launch(Dispatchers.IO) {
        while (!isShutDown()) {
          kc.fetchMessages(config.eventMsgKey)
            .forEach { (userID, datasetID, source) ->
              logger.info("received reconciliation event for dataset {}/{} from source {}", userID, datasetID, source)
              wp.submit { reconcile(userID, datasetID, source) }
            }
        }
      }
    }

    wp.stop()
    kc.close()
    confirmShutdown()
  }

  private fun reconcile(userID: UserID, datasetID: DatasetID, source: EventSource) {
    if (datasetsInProgress.add(datasetID)) {
      try {
        reconciler.reconcile(userID, datasetID, source)
      } finally {
        datasetsInProgress.remove(datasetID)
      }
    } else {
      logger.info("dataset reconciliation already in progress for dataset {}/{}, skipping job", userID, datasetID)
    }
  }
}

