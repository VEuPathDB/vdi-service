package vdi.lib.plugin.mapping

import vdi.model.data.DataType
import vdi.core.config.loadAndCacheStackConfig
import vdi.core.health.RemoteDependencies
import vdi.lib.plugin.client.PluginHandlerClient
import vdi.lib.plugin.client.PluginHandlerClientConfig
import vdi.lib.plugin.registry.PluginRegistry

/**
 * Collection of [PluginHandler] instances mapped by dataset type name.
 */
object PluginHandlers {
  private val mapping: Map<NameVersionPair, PluginHandler>

  init {
    val tmpMap = HashMap<NameVersionPair, PluginHandler>(loadAndCacheStackConfig().vdi.plugins.size)

    loadAndCacheStackConfig().vdi.plugins.forEach { plug ->
      val addr = plug.server.toHostAddress(80u)

      RemoteDependencies.register("Plugin ${plug.displayName}", addr.host, addr.port)

      plug.dataTypes.forEach { dt ->
        val key = NameVersionPair(DataType.of(dt.name), dt.version)

        tmpMap[key] = PluginHandlerImpl(
          key.name,
          PluginHandlerClient(PluginHandlerClientConfig(addr)),
          PluginRegistry[key.name, dt.version]!!
        )
      }
    }

    mapping = tmpMap
  }

  /**
   * Tests whether this [PluginHandlers] instance contains a [PluginHandler] for
   * the given dataset type name.
   *
   * @param type Name of the dataset type to test for.
   *
   * @return `true` if this [PluginHandlers] instance contains a [PluginHandler]
   * for the given dataset type, otherwise `false`.
   */
  fun contains(type: DataType, version: String): Boolean =
    NameVersionPair(type, version) in mapping

  /**
   * Attempts to look up a [PluginHandler] for the given dataset type name.
   *
   * @param type Name of the dataset type for which a [PluginHandler] should be
   * retrieved.
   *
   * @return The [PluginHandler] for the given dataset type, or `null` if no
   * such [PluginHandler] exists.
   */
  operator fun get(type: DataType, version: String): PluginHandler? =
    mapping[NameVersionPair(type, version)]

  data class NameVersionPair(val name: DataType, val version: String)
}
