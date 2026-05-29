package vdi.lane.install

import org.slf4j.Logger
import vdi.core.kafka.EventMessage
import vdi.core.kafka.router.KafkaRouter
import vdi.core.plugin.mapping.PluginHandler
import vdi.core.s3.DatasetObjectStore
import vdi.logging.mark
import vdi.model.meta.InstallTargetID

internal class InstallationContext(
  private val msg: EventMessage,
  val store: DatasetObjectStore,
  val kafka: KafkaRouter,
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

  fun withPlugin(plugin: PluginHandler, target: InstallTargetID) =
    WithPlugin(plugin, target)

  inner class WithPlugin(
    val plugin: PluginHandler,
    val target: InstallTargetID,
  ) {
    val logger = this@InstallationContext.logger.mark(plugin.type, plugin.name, target)

    val eventID
      get() = this@InstallationContext.eventID

    val ownerID
      get() = this@InstallationContext.msg.userID

    val datasetID
      get() = this@InstallationContext.msg.datasetID

    val source
      get() = this@InstallationContext.msg.eventSource

    val type
      get() = plugin.type

    val kafka
      get() = this@InstallationContext.kafka
  }
}