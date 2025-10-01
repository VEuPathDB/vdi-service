package vdi.daemon.events.routing

import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import vdi.model.data.DatasetID
import vdi.model.data.UserID
import vdi.json.JSON
import vdi.daemon.events.routing.model.MinIOEvent
import vdi.daemon.events.routing.model.MinIOEventAction
import vdi.core.util.discardException
import vdi.core.kafka.EventSource
import vdi.core.kafka.router.KafkaRouter
import vdi.core.kafka.router.KafkaRouterFactory
import vdi.logging.logger
import vdi.logging.markedLogger
import vdi.core.modules.AbortCB
import vdi.core.modules.AbstractVDIModule
import vdi.core.util.orElse
import vdi.core.rabbit.RabbitMQEventIterator
import vdi.core.rabbit.RabbitMQEventSource
import vdi.core.s3.files.DataFileType
import vdi.core.s3.files.FlagFileType
import vdi.core.s3.files.MetaFileType
import vdi.core.s3.paths.*

internal class EventRouterImpl(private val config: EventRouterConfig, abortCB: AbortCB)
  : EventRouter
  , AbstractVDIModule(abortCB, logger<EventRouter>())
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
    // Convert the event target object path to a VDPath instance (if possible)
    val path = event.objectKey.toDatasetPathOrNull().orElse {
      // If the event source object path was not for an object in the dataset
      // "directory" structure then warn and ignore it.
      log.warn("received bucket event for unrecognized object: ${event.objectKey}")
      return
    }

    val log = markedLogger<EventRouter>(path.userID, path.datasetID)

    when (event.eventType.action) {
      MinIOEventAction.DELETE -> event.processDeleteEvent(path, log)
      MinIOEventAction.CREATE -> processPutEvent(path, log)
    }
  }

  private suspend fun MinIOEvent.processDeleteEvent(path: DatasetPath<*>, log: Logger) {
    // If the event action was a deletion of an object, then it was a hard
    // delete regardless of the file.  We only remove anything when we remove
    // everything.  This event should be one of many for this specific dataset.

    // Ignore deletes of old versions done by the MinIO internal expiration
    // policy.
    if (records.any { it.source.userAgent == "Internal: [ILM-Expiry]" })
        return

    log.debug("received hard delete event for object {}", objectKey)
    safeSend(path.userID, path.datasetID, kr::sendHardDeleteTrigger)
    return
  }

  private suspend fun processPutEvent(path: DatasetPath<*>, log: Logger) {
    log.debug("demuxing put event for {}", path)

    when (path) {
      is MetaFilePath -> when (path.type) {
        MetaFileType.Metadata -> {
          safeSend(path.userID, path.datasetID, kr::sendUpdateMetaTrigger)

          // Trigger an import here to handle replication race condition.
          safeSend(path.userID, path.datasetID, kr::sendImportTrigger)
        }

        MetaFileType.Manifest -> { /* ignore manifest events */ }
      }

      is DataFilePath -> when (path.type) {
        DataFileType.ImportReady  -> safeSend(path.userID, path.datasetID, kr::sendImportTrigger)
        DataFileType.InstallReady -> safeSend(path.userID, path.datasetID, kr::sendInstallTrigger)
        DataFileType.RawUpload    -> TODO()
      }

      is ShareFilePath -> safeSend(path.userID, path.datasetID, kr::sendShareTrigger)

      is FlagFilePath -> when (path.type) {
        FlagFileType.Delete  -> safeSend(path.userID, path.datasetID, kr::sendSoftDeleteTrigger)
        FlagFileType.Revised -> safeSend(path.userID, path.datasetID, kr::sendSoftDeleteTrigger)
      }

      is DocumentFilePath -> { /* ignore dataset document events */ }
    }
  }

  private suspend fun safeSend(userID: UserID, datasetID: DatasetID, fn: (UserID, DatasetID, EventSource) -> Unit) {
    try {
      log.debug("TRACE - safeSend(userID={}, datasetID={}, fn=...)", userID, datasetID)
      fn(userID, datasetID, EventSource.ObjectStore)
    } catch (e: Throwable) {
      triggerShutdown()
      log.error("failed to send event message for {}/{}", userID, datasetID, e)
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
