package vdi.module.events.routing

import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import vdi.components.common.ShutdownSignal
import vdi.components.datasets.paths.S3Paths
import vdi.components.json.JSON
import vdi.components.kafka.triggers.*
import vdi.components.rabbit.RabbitMQEventIterator
import vdi.components.rabbit.RabbitMQEventSource
import vdi.module.events.routing.config.EventRouterConfig
import vdi.module.events.routing.model.MinIOEvent
import vdi.module.events.routing.model.MinIOEventAction
import vdi.module.events.routing.router.*
import vdi.module.events.routing.router.KafkaRouterFactory

internal class EventRouterImpl(private val config: EventRouterConfig) : EventRouter {

  private val log = LoggerFactory.getLogger(javaClass)

  private val shutdownTrigger = ShutdownSignal()
  private val shutdownConfirm = ShutdownSignal()

  @Volatile
  private var started = false

  override suspend fun start() {
    if (!started) {
      log.info("starting event-router module")

      started = true
      run()
    }
  }

  override suspend fun stop() {
    log.info("triggering event-router shutdown")
    shutdownTrigger.trigger()
    shutdownConfirm.await()
  }

  private suspend fun run() {
    val es: RabbitMQEventSource<MinIOEvent>
    val kr: KafkaRouter

    try {
      es = RabbitMQEventSource(config.rabbitConfig, shutdownTrigger) { JSON.readValue(it) }
      kr = KafkaRouterFactory(config.kafkaConfig).newKafkaRouter()
    } catch (e: Throwable) {
      shutdownTrigger.trigger()
      shutdownConfirm.trigger()
      log.error("failed to create a RabbitMQBucketEventSource", e)
      throw e
    }

    val stream = es.iterator()

    while (!shutdownTrigger.isTriggered() && stream.safeHasNext()) {
      val event = stream.next()

      if (!event.isDatasetEvent(config.s3Bucket)) {
        log.warn("received bucket event that was not a dataset event for object: ${event.objectKey}")
        continue
      }

      val (userID, datasetID, subDir, subPath) = event.objectKey.splitDatasetPath()

      when {
        event.eventType.action == MinIOEventAction.DELETE -> {
          log.debug("received a hard delete event for dataset {} owned by user {}", datasetID, userID)

          safeSend(HardDeleteTrigger(userID, datasetID), kr::sendHardDeleteTrigger)
        }

        subDir == S3Paths.UPLOAD_DIR_NAME                 -> {
          log.debug("received an import event for dataset {} owned by user {}", datasetID, userID)

          safeSend(ImportTrigger(userID, datasetID), kr::sendImportTrigger)
        }

        subPath == S3Paths.DELETE_FLAG_FILE_NAME          -> {
          log.debug("received a soft delete event for dataset {} owned by user {}", datasetID, userID)

          safeSend(SoftDeleteTrigger(userID, datasetID), kr::sendSoftDeleteTrigger)
        }

        subPath.isSharePath()                             -> {
          log.debug("received a share event for dataset {} owned by user {}", datasetID, userID)

          val (recipientID, _) = subPath.splitSharePath()

          safeSend(ShareTrigger(userID, datasetID, recipientID), kr::sendShareTrigger)
        }

        else                                              -> {
          log.debug("received an install event for dataset {} owned by user {}", datasetID, userID)

          safeSend(InstallTrigger(userID, datasetID), kr::sendInstallTrigger)

          if (subPath == S3Paths.META_FILE_NAME) {
            log.debug("received an update-meta event for dataset {} owned by user {}", datasetID, userID)

            safeSend(UpdateMetaTrigger(userID, datasetID), kr::sendUpdateMetaTrigger)
          }
        }
      }
    }

    log.info("shutting down event-router")

    es.close()
    shutdownConfirm.trigger()
  }

  private suspend fun <T> safeSend(event: T, fn: (T) -> Unit) {
    try {
      fn(event)
    } catch (e: Throwable) {
      shutdownTrigger.trigger()
      log.error("failed to send event message to Kafka", e)
      throw e
    }
  }

  private suspend fun RabbitMQEventIterator<MinIOEvent>.safeHasNext() =
    try {
      hasNext()
    } catch (e: Throwable) {
      shutdownTrigger.trigger()
      log.error("failed to poll RabbitMQ for next message", e)
      throw e
    }
}

