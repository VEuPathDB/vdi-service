package vdi.lane.meta

import org.slf4j.Logger
import vdi.core.kafka.EventMessage
import vdi.core.kafka.router.KafkaRouter
import vdi.core.plugin.mapping.PluginHandler
import vdi.core.s3.DatasetObjectStore
import vdi.logging.mark
import vdi.model.meta.DatasetMetadata
import vdi.model.meta.InstallTargetID

class UpdateMetaContext(
  private val message: EventMessage,
  val store: DatasetObjectStore,
  val events: KafkaRouter,
  logger: Logger,
) {
  val logger = logger.mark(message.eventID, message.userID, message.datasetID)

  val datasetID
    get() = message.datasetID

  val eventID
    get() = message.eventID

  val ownerID
    get() = message.userID

  val source
    get() = message.eventSource

  fun withPlugin(meta: DatasetMetadata, plugin: PluginHandler, target: InstallTargetID) =
    WithPlugin(meta, plugin, target)

  inner class WithPlugin(
    val meta: DatasetMetadata,
    val plugin: PluginHandler,
    val target: InstallTargetID,
  ) {
    val logger = this@UpdateMetaContext.logger.mark(meta.type, plugin.name, target)

    val datasetID
      get() = this@UpdateMetaContext.datasetID

    val eventID
      get() = this@UpdateMetaContext.eventID

    val ownerID
      get() = this@UpdateMetaContext.ownerID

    val source
      get() = this@UpdateMetaContext.source

    val store
      get() = this@UpdateMetaContext.store

    val events
      get() = this@UpdateMetaContext.events
  }
}