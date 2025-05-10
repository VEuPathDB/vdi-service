package vdi.lane.delete.hard

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import vdi.component.modules.AbstractVDIModule

internal class HardDeleteTriggerHandlerImpl(private val config: HardDeleteTriggerHandlerConfig, abortCB: (String?) -> Nothing)
  : HardDeleteTriggerHandler
  , AbstractVDIModule("hard-delete-trigger-handler", abortCB)
{
  private val log = LoggerFactory.getLogger(javaClass)

  override suspend fun run() {
    val kc = requireKafkaConsumer(config.hardDeleteTopic, config.kafkaConsumerConfig)

    coroutineScope {
      launch(Dispatchers.IO) {
        while (!isShutDown()) {
          kc.fetchMessages(config.hardDeleteMessageKey)
            .forEach { log.info("received hard-delete event for dataset {}/{} from {}", it.userID, it.datasetID, it.eventSource) }
        }
      }
    }

    log.info("closing kafka client")
    kc.close()
    log.info("kafka client closed")
    confirmShutdown()
  }
}

