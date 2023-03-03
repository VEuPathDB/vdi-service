package vdi.module.events.routing

import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import vdi.components.common.ShutdownSignal
import vdi.components.datasets.paths.*
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
      val path  = event.objectKey.toVDPathOrNull()

      if (path == null) {
        log.warn("received bucket event that was not a dataset event for object: ${event.objectKey}")
        continue
      }

      when {
        // If the event action was a deletion of an object, then it was a hard
        // delete regardless of the file.  We only remove anything when we
        // remove everything.  This event should be one of many for this
        // specific dataset.
        event.eventType.action == MinIOEventAction.DELETE -> {
          log.debug("received a hard delete event for dataset {} owned by user {}", path.datasetID.toString(), path.userID.toString())

          safeSend(HardDeleteTrigger(path.userID, path.datasetID), kr::sendHardDeleteTrigger)
        }

        // If the path was for an upload file then it's an import trigger as we
        // only ever put something in the upload directory when the dataset is
        // first uploaded by the client.
        path is VDUploadPath -> {
          log.debug("received an import event for dataset {} owned by user {}", path.datasetID, path.userID)

          safeSend(ImportTrigger(path.userID, path.datasetID), kr::sendImportTrigger)
        }

        // If the path was to a soft delete flag then we have a soft-delete
        // event.
        path is VDDatasetFilePath && path.subPath == S3Paths.DELETE_FLAG_FILE_NAME -> {
          log.debug("received a soft delete event for dataset {} owned by user {}", path.datasetID, path.userID)

          safeSend(SoftDeleteTrigger(path.userID, path.datasetID), kr::sendSoftDeleteTrigger)
        }

        // If the path is to a share file then we have a share event.
        path is VDDatasetShareFilePath -> {
          log.debug("received a share event for dataset {} owned by user {}", path.datasetID, path.userID)

          safeSend(ShareTrigger(path.userID, path.datasetID, path.recipientID), kr::sendShareTrigger)
        }

        // Else, we have an install event.
        else                                              -> {
          log.debug("received an install event for dataset {} owned by user {}", path.datasetID, path.userID)

          safeSend(InstallTrigger(path.userID, path.datasetID), kr::sendInstallTrigger)

          // If the object that got created was the meta.json file then we also
          // have an update-meta event in addition to our install event.
          //
          // This is because we don't know at this point whether this is the
          // first time the meta.json file was put into the bucket or if this is
          // an update.
          if (path is VDDatasetFilePath && path.subPath == S3Paths.META_FILE_NAME) {
            log.debug("received an update-meta event for dataset {} owned by user {}", path.datasetID, path.userID)

            safeSend(UpdateMetaTrigger(path.userID, path.datasetID), kr::sendUpdateMetaTrigger)
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

