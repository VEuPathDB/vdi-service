package vdi.lane.reconciliation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import java.util.concurrent.ConcurrentHashMap
import vdi.lib.async.WorkerPool
import vdi.lib.kafka.EventSource
import vdi.lib.metrics.Metrics
import vdi.lib.modules.AbortCB
import vdi.lib.modules.AbstractVDIModule

internal class ReconciliationEventHandlerImpl(
  private val config: ReconciliationEventHandlerConfig,
  abortCB: AbortCB
)
  : ReconciliationEventHandler
  , AbstractVDIModule("reconciliation-event-handler", abortCB)
{
  private val log = LoggerFactory.getLogger(javaClass)

  private lateinit var reconciler: DatasetReconciler

  private val datasetsInProgress = ConcurrentHashMap.newKeySet<DatasetID>(32)

  override suspend fun run() {
    val kc = requireKafkaConsumer(config.eventChannel, config.kafkaInConfig)
    val wp = WorkerPool("reconciliation-workers", config.workerCount, config.jobQueueSize) {
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
              log.info("received reconciliation event for dataset {}/{} from source {}", userID, datasetID, source)
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
      log.info("dataset reconciliation already in progress for dataset {}/{}, skipping job", userID, datasetID)
    }
  }
}

