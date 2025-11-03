package vdi.core.plugin.registry

import vdi.config.parse.ConfigurationException
import vdi.core.config.loadAndCacheStackConfig
import vdi.model.data.DataType
import vdi.model.data.DatasetType

object PluginRegistry: Iterable<Pair<DatasetType, PluginDetails>> {
  private val mapping: Map<DatasetType, PluginDetails>
  private val metadata: Map<DatasetType, DataTypeMetadata>

  init {
    val conflicts = HashMap<String, MutableList<String>>(1)
    val tmpCats   = HashMap<DatasetType, DataTypeMetadata>(12)

    mapping = loadAndCacheStackConfig().vdi.plugins
      .asSequence()
      .flatMap { (name, plug) ->
        val details = PluginDetails(
          name,
          plug.projectIDs?.toList() ?: emptyList(),
          plug.typeChangesEnabled ?: false,
        )

        plug.dataTypes.asSequence()
          .onEach { conflicts.computeIfAbsent(it.displayName, { ArrayList(1) }).add(name) }
          .map { DatasetType(DataType.of(it.name), it.version)
            .also { dt -> if (it.category != null) tmpCats[dt] = DataTypeMetadata(it.displayName, it.category!!) } }
          .map { it to details }
      }
      .toMap()

    conflicts.asSequence()
      .filter { (_, v) -> v.size > 1 }
      .map { (k, v) -> "multiple plugins declare type $k: ${v.joinToString(", ")}" }
      .joinToString("\n")
      .takeUnless { it.isBlank() }
      ?.also { throw ConfigurationException(it) }

    metadata = HashMap<DatasetType, DataTypeMetadata>(tmpCats.size)
      .apply { putAll(tmpCats) }
  }

  fun contains(type: DatasetType) = type in mapping

  fun categoryFor(type: DatasetType) = metadata[type]?.category ?: throw MissingDataTypeCategoryException(type)

  fun categoryOrNullFor(type: DatasetType) = metadata[type]?.category

  fun displayNameFor(type: DatasetType) = metadata[type]?.displayName ?: throw MissingDataTypeCategoryException(type)

  fun displayNameOrNullFor(type: DatasetType) = metadata[type]?.displayName

  operator fun get(type: DatasetType) = mapping[type]

  override fun iterator() = asSequence().iterator()

  fun asSequence() =
    sequence { mapping.forEach { (k, v) -> yield(k to v) } }
}
