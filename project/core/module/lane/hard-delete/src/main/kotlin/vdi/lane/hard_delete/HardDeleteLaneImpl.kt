package vdi.lane.hard_delete

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import vdi.logging.logger
import vdi.core.modules.AbortCB
import vdi.core.modules.AbstractVDIModule

internal class HardDeleteLaneImpl(private val config: HardDeleteLaneConfig, abortCB: AbortCB)
  : HardDeleteLane
  , AbstractVDIModule(abortCB, logger<HardDeleteLane>())
{
  override suspend fun run() {
    val kc = requireKafkaConsumer(config.eventChannel, config.kafkaConfig)

    coroutineScope {
      launch(Dispatchers.IO) {
        while (!isShutDown()) {
          kc.fetchMessages(config.eventMsgKey)
            .forEach { logger.info("received hard-delete event for dataset {}/{} from {}", it.userID, it.datasetID, it.eventSource) }
        }
      }
    }

    logger.info("closing kafka client")
    kc.close()
    logger.info("kafka client closed")
    confirmShutdown()
  }
}

