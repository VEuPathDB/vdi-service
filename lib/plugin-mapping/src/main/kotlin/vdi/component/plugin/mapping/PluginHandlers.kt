package vdi.component.plugin.mapping

import org.veupathdb.vdi.lib.common.env.reqHostAddress
import org.veupathdb.vdi.lib.common.field.DataType
import vdi.component.env.EnvKey
import vdi.component.plugin.client.PluginHandlerClient
import vdi.component.plugin.client.PluginHandlerClientConfig
import vdi.component.plugins.PluginRegistry
import vdi.health.RemoteDependencies

/**
 * Collection of [PluginHandler] instances mapped by dataset type name.
 */
object PluginHandlers {
  private val mapping: Map<NameVersionPair, PluginHandler>

  init {
    val env = System.getenv()

    mapping = PluginRegistry.envKeys()
      .map {
        val (dt, version, details) = PluginRegistry[it]!!
        val address = env.reqHostAddress(EnvKey.Handlers.Prefix + it + EnvKey.Handlers.AddressSuffix)

        RemoteDependencies.register("Plugin ${details.displayName}", address.host, address.port)

        NameVersionPair(dt, version) to
          PluginHandlerImpl(dt, PluginHandlerClient(PluginHandlerClientConfig(address)), details)
      }
      .toMap()
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

  fun sequence(): Sequence<Pair<NameVersionPair, PluginHandler>> {
    return mapping.asSequence().map { (k, v) -> k to v }
  }

  data class NameVersionPair(val name: DataType, val version: String)
}
