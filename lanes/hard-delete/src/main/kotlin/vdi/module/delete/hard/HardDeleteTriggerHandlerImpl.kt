package vdi.module.delete.hard

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import vdi.component.modules.VDIServiceModuleBase

internal class HardDeleteTriggerHandlerImpl(private val config: HardDeleteTriggerHandlerConfig)
  : HardDeleteTriggerHandler
  , VDIServiceModuleBase("hard-delete-trigger-handler")
{
  private val log = LoggerFactory.getLogger(javaClass)

  override suspend fun run() {
    val kc = requireKafkaConsumer(config.hardDeleteTopic, config.kafkaConsumerConfig)

    runBlocking {
      launch(Dispatchers.IO) {
        while (!isShutDown()) {
          kc.fetchMessages(config.hardDeleteMessageKey)
            .forEach { (userID, datasetID, source) ->
              log.info("received hard-delete event for dataset {}/{} from {}", userID, datasetID, source)
            }
        }
      }
    }

    log.info("closing kafka client")
    kc.close()
    log.info("kafka client closed")
    confirmShutdown()
  }
}

