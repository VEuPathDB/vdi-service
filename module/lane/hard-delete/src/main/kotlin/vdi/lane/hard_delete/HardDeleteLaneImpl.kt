package vdi.lane.hard_delete

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import vdi.lib.logging.logger
import vdi.lib.modules.AbortCB
import vdi.lib.modules.AbstractVDIModule

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

