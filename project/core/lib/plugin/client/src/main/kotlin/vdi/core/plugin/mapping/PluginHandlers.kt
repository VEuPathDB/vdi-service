package vdi.core.plugin.mapping

import vdi.model.data.DataType
import vdi.core.config.loadAndCacheStackConfig
import vdi.core.health.RemoteDependencies
import vdi.core.plugin.client.PluginHandlerClient
import vdi.core.plugin.client.PluginHandlerClientConfig
import vdi.core.plugin.registry.PluginRegistry
import vdi.model.data.DatasetType

/**
 * Collection of [PluginHandler] instances mapped by dataset type name.
 */
object PluginHandlers {
  private val mapping: Map<DatasetType, PluginHandler>

  init {
    val tmpMap = HashMap<DatasetType, PluginHandler>(loadAndCacheStackConfig().vdi.plugins.size)

    loadAndCacheStackConfig().vdi.plugins.forEach { (_, plug) ->
      val addr = plug.server.toHostAddress(80u)

      RemoteDependencies.register("Plugin ${plug.displayName}", addr.host, addr.port)

      plug.dataTypes.forEach { dt ->
        val key = DatasetType(DataType.of(dt.name), dt.version)

        tmpMap[key] = PluginHandlerImpl(key, PluginHandlerClient(PluginHandlerClientConfig(addr)), PluginRegistry[key]!!)
      }
    }

    mapping = tmpMap
  }

  /**
   * Tests whether this [PluginHandlers] instance contains a [PluginHandler] for
   * the given dataset type name.
   */
  fun contains(type: DatasetType): Boolean =
    type in mapping

  /**
   * Attempts to look up a [PluginHandler] for the given dataset type name.
   *
   * @param type Name of the dataset type for which a [PluginHandler] should be
   * retrieved.
   *
   * @return The [PluginHandler] for the given dataset type, or `null` if no
   * such [PluginHandler] exists.
   */
  operator fun get(type: DatasetType): PluginHandler? =
    mapping[type]
}
