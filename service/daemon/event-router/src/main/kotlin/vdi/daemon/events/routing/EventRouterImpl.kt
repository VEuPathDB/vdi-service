package vdi.daemon.events.routing

import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.json.JSON
import vdi.component.kafka.EventSource
import vdi.component.kafka.router.KafkaRouter
import vdi.component.kafka.router.KafkaRouterFactory
import vdi.component.modules.AbortCB
import vdi.component.modules.AbstractVDIModule
import vdi.component.rabbit.RabbitMQEventIterator
import vdi.component.rabbit.RabbitMQEventSource
import vdi.component.s3.paths.VDDatasetFilePath
import vdi.component.s3.paths.VDDatasetShareFilePath
import vdi.component.s3.paths.toVDPathOrNull
import vdi.daemon.events.routing.model.MinIOEvent
import vdi.daemon.events.routing.model.MinIOEventAction

internal class EventRouterImpl(private val config: EventRouterConfig, abortCB: AbortCB)
  : EventRouter
  , AbstractVDIModule("event-router", abortCB)
{
  private val log = LoggerFactory.getLogger(javaClass)

  private val es: RabbitMQEventSource<MinIOEvent> = runBlocking {
    try {
      log.debug("Connecting to RabbitMQ: {}", config.rabbitConfig.serverAddress)
      RabbitMQEventSource(config.rabbitConfig) { JSON.readValue<MinIOEvent>(it) }
    } catch (e: Throwable) {
      triggerShutdown()
      confirmShutdown()
      log.error("failed to create a RabbitMQEventSource", e)
      throw e
    }
  }

  private val kr: KafkaRouter = runBlocking {
    try {
      KafkaRouterFactory(config.kafkaConfig).newKafkaRouter()
    } catch (e: Throwable) {
      triggerShutdown()
      confirmShutdown()
      log.error("failed to create a KafkaRouterFactory", e)
      throw e
    }
  }

  override suspend fun run() {
    coroutineScope {
      try {
        runRouter(es.iterator())
      } catch (e: Throwable) {
        launch { abortCB(e.message) }
      }

      confirmShutdown()
    }
  }

  override suspend fun onShutdown() {
    es.close()
    kr.close()
  }

  private suspend fun runRouter(stream: RabbitMQEventIterator<MinIOEvent>) {
    // While we haven't been told to shut down, and the stream hasn't yet been
    // closed:
    while (!isShutDown() && stream.safeHasNext()) {

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
        // Ignore deletes of old versions done by the MinIO internal expiration
        // policy.
        if (event.records.any { it.source.userAgent == "Internal: [ILM-Expiry]" })
          continue

        log.debug("received a hard delete event for dataset {}/{} for MinIO key {}", path.userID, path.datasetID, event.objectKey)
        safeSend(path.userID, path.datasetID, kr::sendHardDeleteTrigger)
        continue
      }

      if (path is VDDatasetShareFilePath) {
        log.debug("received a share event for dataset {}/{}", path.userID, path.datasetID)
        safeSend(path.userID, path.datasetID, kr::sendShareTrigger)
        continue
      }

      if (path !is VDDatasetFilePath) {
        log.error("unrecognized VDPath implementation type {}, someone forgot to update the event router", path::class.qualifiedName)
        continue
      }

      when {
        path.isMetaFile -> {
          log.debug("received an metadata event for dataset {}/{}", path.userID, path.datasetID)

          safeSend(path.userID, path.datasetID, kr::sendUpdateMetaTrigger)

          // Trigger an import event here in case the files synced from the
          // opposite campus out of order.  This is needed as both the metadata
          // file and the import-ready zip file are needed to run the import.
          safeSend(path.userID, path.datasetID, kr::sendImportTrigger)
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

          safeSend(path.userID, path.datasetID, kr::sendImportTrigger)
        }

        path.isInstallReadyFile -> {
          log.debug("received an install event for dataset {}/{}", path.userID, path.datasetID)

          safeSend(path.userID, path.datasetID, kr::sendInstallTrigger)
        }

        path.isDeleteFlagFile -> {
          log.debug("received a soft delete event for dataset {}/{}", path.userID, path.datasetID)

          safeSend(path.userID, path.datasetID, kr::sendSoftDeleteTrigger)
        }
      }
    }
  }

  private suspend fun safeSend(userID: UserID, datasetID: DatasetID, fn: (UserID, DatasetID, EventSource) -> Unit) {
    try {
      fn(userID, datasetID, EventSource.ObjectStore)
    } catch (e: Throwable) {
      triggerShutdown()
      log.error("failed to send event message to Kafka", e)
      throw e
    }
  }

  private suspend fun RabbitMQEventIterator<*>.safeHasNext() =
    try {
      hasNext()
    } catch (e: Throwable) {
      log.error("failed to poll RabbitMQ for next message", e)
      throw e
    }
}
