package vdi.module.events.routing

import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.async.ShutdownSignal
import org.veupathdb.vdi.lib.json.JSON
import org.veupathdb.vdi.lib.kafka.model.triggers.*
import org.veupathdb.vdi.lib.kafka.router.KafkaRouterFactory
import org.veupathdb.vdi.lib.rabbit.RabbitMQEventIterator
import org.veupathdb.vdi.lib.rabbit.RabbitMQEventSource
import org.veupathdb.vdi.lib.s3.datasets.paths.*
import vdi.module.events.routing.model.MinIOEvent
import vdi.module.events.routing.model.MinIOEventAction

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

    // Get a RabbitMQ Event Source which we will use to get an event stream
    // later.
    val es = try {
      log.debug("Connecting to RabbitMQ: {}", config.rabbitConfig.serverAddress)
      RabbitMQEventSource(config.rabbitConfig, shutdownTrigger) { JSON.readValue<MinIOEvent>(it) }
    } catch (e: Throwable) {
      shutdownTrigger.trigger()
      shutdownConfirm.trigger()
      log.error("failed to create a RabbitMQEventSource", e)
      throw e
    }

    // Get a Kafka Router which we will use to publish messages to the
    // appropriate Kafka topics as messages come in from RabbitMQ.
    val kr = try {
      KafkaRouterFactory(config.kafkaConfig).newKafkaRouter()
    } catch (e: Throwable) {
      shutdownTrigger.trigger()
      shutdownConfirm.trigger()
      log.error("failed to create a KafkaRouterFactory", e)
      throw e
    }

    // Get a handle on the stream of messages from RabbitMQ.
    val stream = es.iterator()

    // While we haven't been told to shut down, and the stream hasn't yet been
    // closed:
    while (!shutdownTrigger.isTriggered() && stream.safeHasNext()) {

      // Get the next event from the RabbitMQ stream.
      val event = stream.next()

      log.debug("received bucket event for object ${event.objectKey}")

      // Convert the event target object path to a VDPath instance (if possible)
      val path = event.objectKey.toVDPathOrNull()

      // If the event source object path was not for an object in the dataset
      // "directory" structure then warn and ignore it.
      if (path == null) {
        log.warn("received bucket event that was not a dataset event for object: ${event.objectKey}")
        continue
      }

      // If the event action was a deletion of an object, then it was a hard
      // delete regardless of the file.  We only remove anything when we
      // remove everything.  This event should be one of many for this
      // specific dataset.
      if (event.eventType.action == MinIOEventAction.DELETE) {
        log.debug("received a hard delete event for dataset {}/{} for MinIO key {}", path.userID.toString(), path.datasetID.toString(), event.objectKey)
        safeSend(HardDeleteTrigger(path.userID, path.datasetID), kr::sendHardDeleteTrigger)
      }

      if (path is VDDatasetShareFilePath) {
        log.debug("received a share event for dataset {}/{}", path.userID, path.datasetID)
        safeSend(ShareTrigger(path.userID, path.datasetID), kr::sendShareTrigger)
      }

      if (path !is VDDatasetFilePath) {
        log.error("unrecognized VDPath implementation type {}, someone forgot to update the event router", path::class.qualifiedName)
        continue
      }

      when {
        path.isMetaFile -> {
          log.debug("received an metadata event for dataset {}/{}", path.userID, path.datasetID)

          safeSend(UpdateMetaTrigger(path.userID, path.datasetID), kr::sendUpdateMetaTrigger)

          // Trigger an import event here in case the files synced from the
          // opposite campus out of order.  This is needed as both the metadata
          // file and the import-ready zip file are needed to run the import.
          safeSend(ImportTrigger(path.userID, path.datasetID), kr::sendImportTrigger)
        }

        path.isManifestFile -> {
          // We don't care about this event.  The manifest file exists only to
          // allow us to repopulate the internal postgres database's file
          // listing table.
        }

        path.isRawUploadFile -> {
          log.debug("received a raw upload event for dataset {}/{}", path.userID, path.datasetID)
          // TODO: this is a placeholder for when the "robust" async user upload
          //       process is implemented.
        }

        path.isImportReadyFile -> {
          log.debug("received an import event for dataset {}/{}", path.userID, path.datasetID)

          safeSend(ImportTrigger(path.userID, path.datasetID), kr::sendImportTrigger)
        }

        path.isInstallReadyFile -> {
          log.debug("received an install event for dataset {}/{}", path.userID, path.datasetID)

          safeSend(InstallTrigger(path.userID, path.datasetID), kr::sendInstallTrigger)
        }

        path.isDeleteFlagFile -> {
          log.debug("received a soft delete event for dataset {}/{}", path.userID, path.datasetID)

          safeSend(SoftDeleteTrigger(path.userID, path.datasetID), kr::sendSoftDeleteTrigger)
        }
      }
    }

    log.info("shutting down event-router")

    kr.close()
    es.close()
    shutdownConfirm.trigger()
  }

  /**
   * Attempts to send a new event message on a Kafka topic controlled by the
   * given publishing function ([fn]), shutting down the process if sending the
   * event fails.
   *
   * @param event Event message to send.
   *
   * @param fn Kafka publishing function.
   */
  private suspend fun <T> safeSend(event: T, fn: (T) -> Unit) {
    try {
      fn(event)
    } catch (e: Throwable) {
      shutdownTrigger.trigger()
      log.error("failed to send event message to Kafka", e)
      throw e
    }
  }

  private suspend fun RabbitMQEventIterator<*>.safeHasNext() =
    try {
      hasNext()
    } catch (e: Throwable) {
      shutdownTrigger.trigger()
      log.error("failed to poll RabbitMQ for next message", e)
      throw e
    }
}
