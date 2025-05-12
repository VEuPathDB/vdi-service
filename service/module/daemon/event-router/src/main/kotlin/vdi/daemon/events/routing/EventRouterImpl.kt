package vdi.daemon.events.routing

import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.json.JSON
import vdi.daemon.events.routing.model.MinIOEvent
import vdi.daemon.events.routing.model.MinIOEventAction
import vdi.lib.discardException
import vdi.lib.kafka.EventSource
import vdi.lib.kafka.router.KafkaRouter
import vdi.lib.kafka.router.KafkaRouterFactory
import vdi.lib.logging.logger
import vdi.lib.modules.AbortCB
import vdi.lib.modules.AbstractVDIModule
import vdi.lib.orElse
import vdi.lib.rabbit.RabbitMQEventIterator
import vdi.lib.rabbit.RabbitMQEventSource
import vdi.lib.s3.paths.DatasetShareFilePath
import vdi.lib.s3.paths.S3File
import vdi.lib.s3.paths.toDatasetPathOrNull

internal class EventRouterImpl(private val config: EventRouterConfig, abortCB: AbortCB)
  : EventRouter
  , AbstractVDIModule("event-router", abortCB, logger<EventRouter>())
{
  private val es: RabbitMQEventSource<MinIOEvent> = runBlocking {
    try {
      RabbitMQEventSource(config.rabbitConfig) { JSON.readValue<MinIOEvent>(it) }
    } catch (e: Throwable) {
      triggerShutdown()
      confirmShutdown()
      log.error("failed to create a RabbitMQEventSource", e)
      abortCB(e.message)
    }
  }

  private val kr: KafkaRouter = runBlocking {
    try {
      KafkaRouterFactory(config.kafkaConfig).newKafkaRouter()
    } catch (e: Throwable) {
      triggerShutdown()
      confirmShutdown()
      log.error("failed to create a KafkaRouterFactory", e)
      abortCB(e.message)
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
    discardException(es::close)
    discardException(kr::close)
  }

  private suspend fun runRouter(stream: RabbitMQEventIterator<MinIOEvent>) {
    // While we haven't been told to shut down, and the stream hasn't yet been
    // closed:
    while (!isShutDown() && stream.safeHasNext())
      processEvent(stream.next())
  }

  private suspend fun processEvent(event: MinIOEvent) {
    log.debug("received bucket event for object ${event.objectKey}")

    // Convert the event target object path to a VDPath instance (if possible)
    val path = event.objectKey.toDatasetPathOrNull().orElse {
      // If the event source object path was not for an object in the dataset
      // "directory" structure then warn and ignore it.
      log.warn("received bucket event for unrecognized object: ${event.objectKey}")
      return
    }

    // If the event action was a deletion of an object, then it was a hard
    // delete regardless of the file.  We only remove anything when we remove
    // everything.  This event should be one of many for this specific dataset.
    if (event.eventType.action == MinIOEventAction.DELETE) {
      // Ignore deletes of old versions done by the MinIO internal expiration
      // policy.
      if (event.records.any { it.source.userAgent == "Internal: [ILM-Expiry]" })
        return

      log.debug("{}/{}: received hard delete event for object {}", path.userID, path.datasetID, event.objectKey)
      safeSend(path.userID, path.datasetID, kr::sendHardDeleteTrigger)
      return
    }

    when (path.file) {
      S3File.Metadata -> {
        log.debug("{}/{}: received metadata event", path.userID, path.datasetID)

        safeSend(path.userID, path.datasetID, kr::sendUpdateMetaTrigger)

        // Trigger an import here to handle replication race condition.
        safeSend(path.userID, path.datasetID, kr::sendImportTrigger)
      }

      // data revision markers trigger dataset uninstallation
      S3File.RevisionFlag -> {
        log.debug("{}/{}: received data revision event", path.userID, path.datasetID)
        safeSend(path.userID, path.datasetID, kr::sendSoftDeleteTrigger)
      }

      S3File.ImportReadyZip  -> {
        log.debug("{}/{}: received import event", path.userID, path.datasetID)
        safeSend(path.userID, path.datasetID, kr::sendImportTrigger)
      }

      S3File.ShareOffer -> {
        log.debug("{}/{}: received share offer event for recipient {}", path.userID, path.datasetID, (path as DatasetShareFilePath).recipientID)
        safeSend(path.userID, path.datasetID, kr::sendShareTrigger)
      }
      S3File.ShareReceipt -> {
        log.debug("{}/{}: received share receipt event for recipient {}", path.userID, path.datasetID, (path as DatasetShareFilePath).recipientID)
        safeSend(path.userID, path.datasetID, kr::sendShareTrigger)
      }

      S3File.InstallReadyZip -> {
        log.debug("{}/{}: received install event", path.userID, path.datasetID)
        safeSend(path.userID, path.datasetID, kr::sendInstallTrigger)
      }

      S3File.DeleteFlag -> {
        log.debug("{}/{}: received a soft delete event", path.userID, path.datasetID)
        safeSend(path.userID, path.datasetID, kr::sendSoftDeleteTrigger)
      }

      // Not yet implemented
      S3File.RawUploadZip -> TODO()

      // Ignore manifest file updates, they are read at update time or as part
      // of reconciliation.
      else -> {}
    }
  }

  private suspend fun safeSend(userID: UserID, datasetID: DatasetID, fn: (UserID, DatasetID, EventSource) -> Unit) {
    try {
      fn(userID, datasetID, EventSource.ObjectStore)
    } catch (e: Throwable) {
      triggerShutdown()
      log.error("failed to send event message to Kafka", e)
      abortCB(e.message)
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
