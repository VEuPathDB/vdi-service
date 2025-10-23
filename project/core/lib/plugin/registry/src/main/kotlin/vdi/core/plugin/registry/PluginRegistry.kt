package vdi.core.plugin.registry

import vdi.config.parse.ConfigurationException
import vdi.core.config.loadAndCacheStackConfig
import vdi.model.data.DataType
import vdi.model.data.DatasetType

object PluginRegistry: Iterable<Pair<DatasetType, PluginDetails>> {
  private val mapping: Map<DatasetType, PluginDetails>
  private val categories: Map<DatasetType, String>

  init {
    val conflicts = HashMap<DatasetType, MutableList<String>>(1)
    val tmpCats   = HashMap<DatasetType, String>(12)

    mapping = loadAndCacheStackConfig().vdi.plugins
      .asSequence()
      .flatMap { (name, plug) ->
        val details = PluginDetails(
          name,
          plug.projectIDs?.toList() ?: emptyList(),
          plug.typeChangesEnabled,
          plug.maxFileSize
        )

        plug.dataTypes.asSequence()
          .map { DatasetType(DataType.of(it.name), it.version).also { dt -> tmpCats[dt] = it.category } }
          .onEach { conflicts.computeIfAbsent(it, { ArrayList(1) }).add(name) }
          .map { it to details }
      }
      .toMap()

    conflicts.asSequence()
      .filter { (_, v) -> v.size > 1 }
      .map { (k, v) -> "multiple plugins declare type $k: ${v.joinToString(", ")}" }
      .joinToString("\n")
      .takeUnless { it.isBlank() }
      ?.also { throw ConfigurationException(it) }

    categories = HashMap<DatasetType, String>(tmpCats.size).apply { putAll(tmpCats) }
  }

  fun contains(type: DatasetType) = type in mapping

  fun categoryFor(type: DatasetType) = categories[type] ?: throw MissingDataTypeCategoryException(type)

  fun categoryOrNullFor(type: DatasetType) = categories[type]

  operator fun get(type: DatasetType) = mapping[type]

  override fun iterator() = asSequence().iterator()

  fun asSequence() =
    sequence { mapping.forEach { (k, v) -> yield(k to v) } }
}
