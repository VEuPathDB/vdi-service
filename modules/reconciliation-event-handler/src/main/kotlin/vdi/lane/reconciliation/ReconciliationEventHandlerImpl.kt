package vdi.lane.reconciliation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.async.WorkerPool
import org.veupathdb.vdi.lib.db.cache.CacheDB
import vdi.component.metrics.Metrics
import vdi.component.modules.VDIServiceModuleBase

internal class ReconciliationEventHandlerImpl(private val config: ReconciliationEventHandlerConfig)
  : ReconciliationEventHandler
  , VDIServiceModuleBase("reconciliation-event-handler")
{
  private val log = LoggerFactory.getLogger(javaClass)

  override suspend fun run() {
    val kc = requireKafkaConsumer(config.kafkaTopic, config.kafkaConsumerConfig)
    val wp = WorkerPool("reconciliation-workers", config.jobQueueSize.toInt(), config.workerPoolSize.toInt()) {
      Metrics.ReconciliationHandler.queueSize.inc(it.toDouble())
    }

    val reconciler = DatasetReconciler(
      eventRouter = requireKafkaRouter(config.kafkaRouterConfig),
      datasetManager = requireDatasetManager(config.s3Config, config.s3Bucket)
    )

    runBlocking {
      launch(Dispatchers.IO) {
        while (!isShutDown()) {
          kc.fetchMessages(config.kafkaMessageKey)
            .forEach { (userID, datasetID, source) ->
              log.info("received reconciliation event for dataset {}/{} from source {}", userID, datasetID, source)
              wp.submit { reconciler.reconcile(userID, datasetID) }
            }
        }

        wp.stop()
      }

      wp.start()
    }

    confirmShutdown()
  }
}

