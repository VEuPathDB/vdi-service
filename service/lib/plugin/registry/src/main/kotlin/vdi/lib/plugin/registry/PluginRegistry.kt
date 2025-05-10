package vdi.lib.plugin.registry

import org.veupathdb.vdi.lib.common.field.DataType
import vdi.lib.config.loadAndCacheStackConfig
import vdi.lib.config.vdi.PluginConfig

object PluginRegistry : Iterable<Triple<DataType, String, PluginDetails>> {
  private val mapping = HashMap<KeyPair, PluginDetails>(8)

  init {
    init(loadAndCacheStackConfig().vdi.plugins)
  }

  @Suppress("MemberVisibilityCanBePrivate")
  fun init(plugins: Iterable<PluginConfig>) {
    // Only needed for testing, but doesn't hurt anything in general.
    mapping.clear()

    plugins.forEach { plug ->
      val details = PluginDetails(
        plug.displayName,
        plug.projectIDs?.toList() ?: emptyList(),
        plug.typeChangesEnabled ?: false,
      )

      plug.dataTypes.forEach { dt ->
        val kp = KeyPair(DataType.of(dt.name), dt.version)

        if (kp in mapping)
          throw IllegalStateException("bad config: multiple plugins declare data type $kp")

        mapping[kp] = details
      }
    }
  }

  fun contains(type: DataType, version: String) =
    KeyPair(type, version) in mapping

  operator fun get(type: DataType, version: String) =
    mapping[KeyPair(type, version)]

  operator fun get(type: String, version: String) =
    mapping[KeyPair(DataType.of(type), version)]

  override fun iterator() =
    asSequence().iterator()

  fun asSequence() =
    sequence { mapping.forEach { (k, v) -> yield(Triple(k.type, k.version, v)) } }

  private data class KeyPair(val type: DataType, val version: String)
}
