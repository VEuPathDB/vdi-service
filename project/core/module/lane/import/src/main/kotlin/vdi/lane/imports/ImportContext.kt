package vdi.lane.imports

import org.slf4j.Logger
import vdi.core.kafka.EventMessage
import vdi.core.plugin.mapping.PluginHandler
import vdi.core.s3.DatasetDirectory
import vdi.core.s3.DatasetObjectStore
import vdi.logging.mark
import vdi.model.data.DatasetMetadata

internal class ImportContext(
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

  fun withPlugin(meta: DatasetMetadata, plugin: PluginHandler, datasetDir: DatasetDirectory) =
    WithPlugin(this, meta, plugin, datasetDir)

  class WithPlugin(
    private val root: ImportContext,
    val meta: DatasetMetadata,
    val plugin: PluginHandler,
    val datasetDir: DatasetDirectory,
  ) {
    val logger = root.logger.mark(meta.type, plugin.name)

    val eventID
      get() = root.msg.eventID

    val ownerID
      get() = root.msg.userID

    val datasetID
      get() = root.msg.datasetID

    val source
      get() = root.msg.eventSource

    val type
      get() = meta.type
  }
}