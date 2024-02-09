package vdi.lane.reconciliation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.async.WorkerPool
import org.veupathdb.vdi.lib.kafka.model.triggers.HardDeleteTrigger
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager
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
    val dm = DatasetManager(requireS3Bucket(requireS3Client(config.s3Config), config.s3Bucket))
    val kr = requireKafkaRouter(config.kafkaRouterConfig)

    runBlocking {
      launch(Dispatchers.IO) {
        while (!isShutDown()) {
          kc.fetchMessages(config.kafkaMessageKey, HardDeleteTrigger::class)
            .forEach { (userID, datasetID) ->
              log.info("received reconciliation event for dataset {}/{}", userID, datasetID)
              wp.submit { DatasetReconciler.reconcile(userID, datasetID, dm.getDatasetDirectory(userID, datasetID), kr) }
            }
        }

        wp.stop()
      }

      wp.start()
    }

    confirmShutdown()
  }
}

