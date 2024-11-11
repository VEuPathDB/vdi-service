package vdi.component.db.app

internal class AppDBRegistryCollection private constructor(
  private val byKey: Map<String, AppDBRegistryEntry>?,
  private val fallback: AppDBRegistryEntry?,
) : Iterable<Pair<String, AppDBRegistryEntry>> {
  operator fun get(pluginName: String) =
    byKey?.get(pluginName) ?: fallback

  override fun iterator() = sequence().iterator()

  fun sequence() = sequence {
    byKey?.also {
      for ((k, v) in it) {
        yield(k to v)
      }
    }

    fallback?.also { yield("*" to it) }
  }

  class Builder {
    private var byKey: MutableMap<String, AppDBRegistryEntry>? = null
    private var fallback: AppDBRegistryEntry? = null

    fun put(fallback: AppDBRegistryEntry) {
      if (this.fallback != null)
        throw IllegalStateException("Multiple fallback databases specified for ${fallback.name} (db config has no plugin binding).")

      this.fallback = fallback
    }

    fun put(pluginName: String, entry: AppDBRegistryEntry) {
      if (byKey == null)
        byKey = mutableMapOf(pluginName to entry)
      else if (pluginName in byKey!!)
        throw IllegalStateException("Multiple database configs attempting to bind to plugin $pluginName for ${entry.name}")
      else
        byKey!![pluginName] = entry
    }

    fun build() = AppDBRegistryCollection(byKey, fallback)
  }
}
