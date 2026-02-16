package vdi.daemon.events.routing

import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import vdi.core.kafka.EventSource
import vdi.core.kafka.router.KafkaRouter
import vdi.core.kafka.router.KafkaRouterFactory
import vdi.core.modules.AbortCB
import vdi.core.modules.AbstractVDIModule
import vdi.core.rabbit.RabbitMQEventIterator
import vdi.core.rabbit.RabbitMQEventSource
import vdi.core.s3.files.FileName
import vdi.core.s3.paths.*
import vdi.core.util.discardException
import vdi.core.util.orElse
import vdi.daemon.events.routing.model.MinIOEvent
import vdi.daemon.events.routing.model.MinIOEventAction
import vdi.json.JSON
import vdi.logging.logger
import vdi.logging.markedLogger
import vdi.model.meta.DatasetID
import vdi.model.meta.UserID

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
      logger.error("failed to create a RabbitMQEventSource", e)
      abortCB(e.message)
    }
  }

  private val kr: KafkaRouter = runBlocking {
    try {
      KafkaRouterFactory(config.kafkaConfig).newKafkaRouter()
    } catch (e: Throwable) {
      triggerShutdown()
      confirmShutdown()
      logger.error("failed to create a KafkaRouterFactory", e)
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
      logger.warn("received bucket event for unrecognized object: ${event.objectKey}")
      return
    }

    val log = markedLogger<EventRouter>(ownerID = path.userID, datasetID = path.datasetID)

    when (event.eventType.action) {
      MinIOEventAction.DELETE -> event.processDeleteEvent(path, log)
      MinIOEventAction.CREATE -> processPutEvent(path, log)
    }
  }

  private suspend fun MinIOEvent.processDeleteEvent(path: DatasetPath, log: Logger) {
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

  private suspend fun processPutEvent(path: DatasetPath, log: Logger) {
    log.debug("demuxing put event for {}", path)

    when (path) {
      is MetaFilePath -> when (path.fileName) {
        FileName.MetadataFile -> {
          safeSend(path.userID, path.datasetID, kr::sendUpdateMetaTrigger)

          // Trigger an import here to handle replication race condition.
          safeSend(path.userID, path.datasetID, kr::sendImportTrigger)
        }

        FileName.ManifestFile -> { /* ignore manifest events */ }

        FileName.UploadErrorFile -> { /* ignore manifest events */ }
      }

      is DataFilePath -> when (path.fileName) {
        FileName.ImportReadyFile  -> safeSend(path.userID, path.datasetID, kr::sendImportTrigger)
        FileName.InstallReadyFile -> safeSend(path.userID, path.datasetID, kr::sendInstallTrigger)
        FileName.RawUploadFile    -> TODO()
      }

      is ShareFilePath -> safeSend(path.userID, path.datasetID, kr::sendShareTrigger)

      is FlagFilePath -> when (path.fileName) {
        FileName.DeleteFlagFile  -> safeSend(path.userID, path.datasetID, kr::sendSoftDeleteTrigger)
        FileName.RevisedFlagFile -> safeSend(path.userID, path.datasetID, kr::sendSoftDeleteTrigger)
      }

      is VariablePropsFilePath -> safeSend(path.userID, path.datasetID, kr::sendUpdateMetaTrigger)

      is DocumentFilePath -> { /* ignore dataset document events */ }
    }
  }

  private suspend fun safeSend(userID: UserID, datasetID: DatasetID, fn: (UserID, DatasetID, EventSource) -> Unit) {
    try {
      logger.trace("safeSend(userID={}, datasetID={}, fn=...)", userID, datasetID)
      fn(userID, datasetID, EventSource.ObjectStore)
    } catch (e: Throwable) {
      triggerShutdown()
      logger.error("failed to send event message for {}/{}", userID, datasetID, e)
      abortCB(e.message)
    }
  }

  private suspend fun RabbitMQEventIterator<*>.safeHasNext() =
    try {
      hasNext()
    } catch (e: Throwable) {
      logger.error("failed to poll RabbitMQ for next message", e)
      throw e
    }
}
