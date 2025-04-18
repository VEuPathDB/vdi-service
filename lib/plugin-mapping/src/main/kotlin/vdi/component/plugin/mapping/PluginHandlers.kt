package vdi.component.plugin.mapping

import org.veupathdb.vdi.lib.common.env.Environment
import org.veupathdb.vdi.lib.common.env.optSet
import org.veupathdb.vdi.lib.common.env.reqHostAddress
import org.veupathdb.vdi.lib.common.env.require
import org.veupathdb.vdi.lib.common.field.DataType
import vdi.component.env.EnvKey
import vdi.component.plugin.client.PluginHandlerClient
import vdi.component.plugin.client.PluginHandlerClientConfig
import vdi.health.RemoteDependencies

/**
 * Collection of [PluginHandler] instances mapped by dataset type name.
 */
object PluginHandlers {
  private val mapping = HashMap<NameVersionPair, PluginHandler>(16)

  init {
    init(System.getenv())
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

  internal fun init(env: Environment) {
    // Only needed for testing, but doesn't hurt anything in general.
    mapping.clear()

    val seen = HashSet<String>()

    env.keys.asSequence()
      .filter { it.startsWith(EnvKey.Handlers.Prefix) }
      .map { parseEnvKeyName(it) }
      .filterNotNull()
      .filter { !seen.contains(it) }
      .forEach { key ->
        parseEnvironmentChunk(env, key)
        seen.add(key)
      }
  }

  private fun parseEnvKeyName(key: String): String? {
    return when {
      key.endsWith(EnvKey.Handlers.DisplayNameSuffix) -> substringEnvKeyName(key, EnvKey.Handlers.DisplayNameSuffix)
      key.endsWith(EnvKey.Handlers.NameSuffix)        -> substringEnvKeyName(key, EnvKey.Handlers.NameSuffix)
      key.endsWith(EnvKey.Handlers.AddressSuffix)     -> substringEnvKeyName(key, EnvKey.Handlers.AddressSuffix)
      key.endsWith(EnvKey.Handlers.ProjectIDsSuffix)  -> substringEnvKeyName(key, EnvKey.Handlers.ProjectIDsSuffix)
      key.endsWith(EnvKey.Handlers.VersionSuffix)     -> substringEnvKeyName(key, EnvKey.Handlers.VersionSuffix)
      else                                            -> null
    }
  }

  @Suppress("NOTHING_TO_INLINE")
  private inline fun substringEnvKeyName(key: String, suffix: String) =
    key.substring(EnvKey.Handlers.Prefix.length, key.length - suffix.length)

  private fun parseEnvironmentChunk(env: Environment, key: String) {
    val name          = DataType.of(env.require(EnvKey.Handlers.Prefix + key + EnvKey.Handlers.NameSuffix))
    val dispName      = env.require(EnvKey.Handlers.Prefix + key + EnvKey.Handlers.DisplayNameSuffix)
    val address       = env.reqHostAddress(EnvKey.Handlers.Prefix + key + EnvKey.Handlers.AddressSuffix)
    val version       = env.require(EnvKey.Handlers.Prefix + key + EnvKey.Handlers.VersionSuffix)
    val projects      = env.optSet(EnvKey.Handlers.Prefix + key + EnvKey.Handlers.ProjectIDsSuffix) ?: emptySet()

    mapping[NameVersionPair(name, version)] = PluginHandlerImpl(
      name,
      dispName,
      PluginHandlerClient(PluginHandlerClientConfig(address)),
      projects,
    )

    RemoteDependencies.register("Plugin $dispName", address.host, address.port)
  }

  data class NameVersionPair(val name: DataType, val version: String)
}
