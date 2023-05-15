package org.veupathdb.vdi.lib.handler.mapping

import org.veupathdb.vdi.lib.common.env.*
import org.veupathdb.vdi.lib.handler.client.PluginHandlerClient
import org.veupathdb.vdi.lib.handler.client.PluginHandlerClientConfig

/**
 * Collection of [PluginHandler] instances mapped by dataset type name.
 */
object PluginHandlers {
  private val mapping = HashMap<String, PluginHandler>(12)

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
  operator fun contains(type: String): Boolean = type in mapping

  /**
   * Attempts to look up a [PluginHandler] for the given dataset type name.
   *
   * @param type Name of the dataset type for which a [PluginHandler] should be
   * retrieved.
   *
   * @return The [PluginHandler] for the given dataset type, or `null` if no
   * such [PluginHandler] exists.
   */
  operator fun get(type: String): PluginHandler? = mapping[type]

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
      key.endsWith(EnvKey.Handlers.NameSuffix)       -> substringEnvKeyName(key, EnvKey.Handlers.NameSuffix)
      key.endsWith(EnvKey.Handlers.AddressSuffix)    -> substringEnvKeyName(key, EnvKey.Handlers.AddressSuffix)
      key.endsWith(EnvKey.Handlers.ProjectIDsSuffix) -> substringEnvKeyName(key, EnvKey.Handlers.ProjectIDsSuffix)
      else                                           -> null
    }
  }

  @Suppress("NOTHING_TO_INLINE")
  private inline fun substringEnvKeyName(key: String, suffix: String) =
    key.substring(EnvKey.Handlers.Prefix.length, key.length - suffix.length)

  private fun parseEnvironmentChunk(env: Environment, key: String) {
    val name     = env.require(EnvKey.Handlers.Prefix + key + EnvKey.Handlers.NameSuffix)
    val address  = env.reqHostAddress(EnvKey.Handlers.Prefix + key + EnvKey.Handlers.AddressSuffix)
    val projects = env.optSet(EnvKey.Handlers.Prefix + key + EnvKey.Handlers.ProjectIDsSuffix) ?: emptySet()

    mapping[name] = PluginHandlerImpl(name, PluginHandlerClient(PluginHandlerClientConfig(address)), projects)
  }
}

