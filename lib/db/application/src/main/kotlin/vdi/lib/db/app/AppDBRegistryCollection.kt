package vdi.lib.db.app

import org.veupathdb.vdi.lib.common.field.DataType

class AppDBRegistryCollection private constructor(
  private val byKey: Map<DataType, AppDBRegistryEntry>?,
  private val fallback: AppDBRegistryEntry?,
) : Iterable<Pair<DataType, AppDBRegistryEntry>> {
  fun keys(): Iterable<DataType> = byKey?.keys ?: emptyList()

  operator fun get(dataType: DataType) =
    byKey?.get(dataType) ?: fallback

  override fun iterator() = sequence().iterator()

  fun sequence() = sequence {
    byKey?.also {
      for ((k, v) in it) {
        yield(k to v)
      }
    }

    fallback?.also { yield(DataType.of("*") to it) }
  }

  internal class Builder {
    private var byKey: MutableMap<DataType, AppDBRegistryEntry>? = null
    private var fallback: AppDBRegistryEntry? = null

    fun put(fallback: AppDBRegistryEntry) {
      if (this.fallback != null)
        throw IllegalStateException("Multiple fallback databases specified for ${fallback.name} (db config has no plugin binding).")

      this.fallback = fallback
    }

    fun put(dataType: DataType, entry: AppDBRegistryEntry) {
      if (byKey == null)
        byKey = mutableMapOf(dataType to entry)
      else if (dataType in byKey!!)
        throw IllegalStateException("Multiple database configs attempting to bind to plugin $dataType for ${entry.name}")
      else
        byKey!![dataType] = entry
    }

    fun build() = AppDBRegistryCollection(byKey, fallback)
  }
}
