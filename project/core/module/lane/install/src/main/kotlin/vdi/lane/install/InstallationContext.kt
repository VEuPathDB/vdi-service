package vdi.lane.install

import org.slf4j.Logger
import vdi.core.kafka.EventMessage
import vdi.core.plugin.mapping.PluginHandler
import vdi.core.s3.DatasetObjectStore
import vdi.logging.mark
import vdi.model.meta.DatasetMetadata
import vdi.model.meta.InstallTargetID

internal class InstallationContext(
  private val msg: EventMessage,
  val store: DatasetObjectStore,
  logger: Logger,
) {
  val logger = logger.mark(msg.eventID, msg.userID, msg.datasetID)

  val eventID
    get() = msg.eventID

  val ownerID
    get() = msg.userID

  val datasetID
    get() = msg.datasetID

  val source
    get() = msg.eventSource

  fun withPlugin(meta: DatasetMetadata, plugin: PluginHandler, target: InstallTargetID) =
    WithPlugin(this, meta, plugin, target)

  class WithPlugin(
    private val root: InstallationContext,
    val meta: DatasetMetadata,
    val plugin: PluginHandler,
    val target: InstallTargetID,
  ) {
    val logger = root.logger.mark(meta.type, plugin.name, target)

    val eventID
      get() = root.msg.eventID

    val ownerID
      get() = root.msg.userID

    val datasetID
      get() = root.msg.datasetID

    val source
      get() = root.msg.eventSource

    val type
      get() = plugin.type
  }
}