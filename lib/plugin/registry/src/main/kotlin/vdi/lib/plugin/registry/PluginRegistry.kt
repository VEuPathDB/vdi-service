@file:Suppress("NOTHING_TO_INLINE")
package vdi.lib.plugin.registry

import org.veupathdb.vdi.lib.common.env.optBool
import org.veupathdb.vdi.lib.common.env.optSet
import org.veupathdb.vdi.lib.common.env.require
import org.veupathdb.vdi.lib.common.field.DataType
import vdi.lib.env.EnvKey
import vdi.lib.env.Environment

object PluginRegistry : Iterable<Triple<DataType, String, PluginDetails>> {
  private val mapping = HashMap<KeyPair, PluginDetails>(8)
  private val byEnvKey = HashMap<String, KeyPair>(8)

  init {
    init(System.getenv())
  }

  internal fun init(env: Environment) {
    // Only needed for testing, but doesn't hurt anything in general.
    mapping.clear()
    byEnvKey.clear()

    // Tracks the plugin specific environment key name.
    // Env key format: {COMMON_PREFIX}_{PLUGIN_KEY}_{COMMON_SUFFIX}
    val seenPluginKeys = HashSet<String>()

    env.keys.asSequence()
      .filter { it.startsWith(EnvKey.Handlers.Prefix) }
      .map { parseEnvKeyName(it) }
      .filterNotNull()
      .filter { !seenPluginKeys.contains(it) }
      .forEach { key ->
        val (kp, det) = parseEnvironmentChunk(env, key)
        mapping[kp] = det
        byEnvKey[key] = kp
        seenPluginKeys.add(key)
      }
  }

  fun contains(type: DataType, version: String) =
    KeyPair(type, version) in mapping

  operator fun get(type: DataType, version: String) =
    mapping[KeyPair(type, version)]

  operator fun get(name: String) =
    byEnvKey[name]?.let { Triple(it.type, it.version, mapping[it]!!) }

  fun envKeys() =
    sequence { byEnvKey.forEach { yield(it.key) } }

  override fun iterator() =
    asSequence().iterator()

  fun asSequence() =
    sequence { mapping.forEach { (k, v) -> yield(Triple(k.type, k.version, v)) } }

  private fun parseEnvKeyName(key: String): String? {
    return when {
      key.endsWith(EnvKey.Handlers.DisplayNameSuffix)
        -> substringEnvKeyName(key, EnvKey.Handlers.DisplayNameSuffix)
      key.endsWith(EnvKey.Handlers.NameSuffix)
        -> substringEnvKeyName(key, EnvKey.Handlers.NameSuffix)
      key.endsWith(EnvKey.Handlers.AddressSuffix)
        -> substringEnvKeyName(key, EnvKey.Handlers.AddressSuffix)
      key.endsWith(EnvKey.Handlers.ProjectIDsSuffix)
        -> substringEnvKeyName(key, EnvKey.Handlers.ProjectIDsSuffix)
      key.endsWith(EnvKey.Handlers.VersionSuffix)
        -> substringEnvKeyName(key, EnvKey.Handlers.VersionSuffix)
      key.endsWith(EnvKey.Handlers.TypeChangesEnabledSuffix) ->
        substringEnvKeyName(key, EnvKey.Handlers.TypeChangesEnabledSuffix)
      else
        -> null
    }
  }

  private inline fun substringEnvKeyName(key: String, suffix: String) =
    key.substring(EnvKey.Handlers.Prefix.length, key.length - suffix.length)

  private fun parseEnvironmentChunk(env: Environment, key: String): Pair<KeyPair, PluginDetails> {
    val name        = env.require(EnvKey.Handlers.Prefix + key + EnvKey.Handlers.NameSuffix)
    val displayName = env.require(EnvKey.Handlers.Prefix + key + EnvKey.Handlers.DisplayNameSuffix)
    val version     = env.require(EnvKey.Handlers.Prefix + key + EnvKey.Handlers.VersionSuffix)
    val projects    = env.optSet(EnvKey.Handlers.Prefix + key + EnvKey.Handlers.ProjectIDsSuffix)
    val changing    = env.optBool(EnvKey.Handlers.Prefix + key + EnvKey.Handlers.TypeChangesEnabledSuffix)

    return KeyPair(DataType.of(name), version) to PluginDetails(displayName, projects ?: emptySet(), changing ?: false)
  }

  private data class KeyPair(val type: DataType, val version: String)
}
