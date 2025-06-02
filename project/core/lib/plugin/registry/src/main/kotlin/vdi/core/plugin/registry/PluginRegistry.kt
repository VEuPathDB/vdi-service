package vdi.core.plugin.registry

import vdi.model.data.DatasetType
import vdi.config.loadAndCacheStackConfig
import vdi.config.parse.ConfigurationException
import vdi.model.data.DataType

object PluginRegistry: Iterable<Pair<DatasetType, PluginDetails>> {
  private val mapping: Map<DatasetType, PluginDetails>

  init {
    val conflicts = HashMap<DatasetType, List<String>>(1)

    mapping = loadAndCacheStackConfig().vdi.plugins.values
      .asSequence()
      .flatMap { plug ->
        val details = PluginDetails(
          plug.displayName,
          plug.projectIDs?.toList() ?: emptyList(),
          plug.typeChangesEnabled ?: false,
        )

        plug.dataTypes.asSequence()
          .map { DatasetType(DataType.of(it.name), it.version) }
          .onEach {
            if (it in conflicts)
              conflicts[it] = conflicts[it]!! + plug.displayName
            else
              conflicts[it] = listOf(plug.displayName)
          }
          .map { it to details }
      }
      .toMap()

    conflicts.asSequence()
      .filter { (_, v) -> v.size > 1 }
      .map { (k, v) -> "multiple plugins declare type $k: ${v.joinToString(", ")}" }
      .joinToString("\n")
      .takeUnless { it.isBlank() }
      ?.also { throw ConfigurationException(it) }
  }

  fun contains(type: DatasetType) = type in mapping

  operator fun get(type: DatasetType) = mapping[type]

  override fun iterator() = asSequence().iterator()

  fun asSequence() =
    sequence { mapping.forEach { (k, v) -> yield(k to v) } }
}
