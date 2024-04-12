package vdi.lane.reconciliation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.component.async.WorkerPool
import vdi.component.kafka.EventSource
import vdi.component.metrics.Metrics
import vdi.component.modules.AbstractVDIModule
import java.util.concurrent.ConcurrentHashMap

internal class ReconciliationEventHandlerImpl(private val config: ReconciliationEventHandlerConfig)
  : ReconciliationEventHandler
  , AbstractVDIModule("reconciliation-event-handler")
{
  private val log = LoggerFactory.getLogger(javaClass)

  private lateinit var reconciler: DatasetReconciler

  private val datasetsInProgress = ConcurrentHashMap.newKeySet<DatasetID>(32)

  override suspend fun run() {
    val kc = requireKafkaConsumer(config.kafkaTopic, config.kafkaConsumerConfig)
    val wp = WorkerPool("reconciliation-workers", config.jobQueueSize.toInt(), config.workerPoolSize.toInt()) {
      Metrics.ReconciliationHandler.queueSize.inc(it.toDouble())
    }

    reconciler = DatasetReconciler(
      eventRouter = requireKafkaRouter(config.kafkaRouterConfig),
      datasetManager = requireDatasetManager(config.s3Config, config.s3Bucket)
    )

    coroutineScope {
      launch(Dispatchers.IO) {
        while (!isShutDown()) {
          kc.fetchMessages(config.kafkaMessageKey)
            .forEach { (userID, datasetID, source) ->
              log.info("received reconciliation event for dataset {}/{} from source {}", userID, datasetID, source)
              wp.submit { reconcile(userID, datasetID, source) }
            }
        }

        wp.stop()
      }

      wp.start()
    }

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

