package vdi.module.delete.hard

import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.async.WorkerPool
import org.veupathdb.vdi.lib.kafka.model.triggers.HardDeleteTrigger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import vdi.component.modules.VDIServiceModuleBase

internal class HardDeleteTriggerHandlerImpl(private val config: HardDeleteTriggerHandlerConfig)
  : HardDeleteTriggerHandler
  , VDIServiceModuleBase("hard-delete-trigger-handler")
{
  private val log = LoggerFactory.getLogger(javaClass)

  override suspend fun run() {
    val kc = requireKafkaConsumer(config.hardDeleteTopic, config.kafkaConsumerConfig)
    val wp = WorkerPool("hard-delete-workers", config.jobQueueSize.toInt(), config.workerPoolSize.toInt())

    runBlocking {
      launch(Dispatchers.IO) {
        while (!isShutDown()) {
          kc.fetchMessages(config.hardDeleteMessageKey, HardDeleteTrigger::class)
            .forEach { (userID, datasetID) ->
              log.info("received hard-delete event for dataset {}, owned by user {}", userID, datasetID)
            }
        }

        wp.stop()
      }

      wp.start()
    }

    confirmShutdown()
  }
}

