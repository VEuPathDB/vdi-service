package vdi.core.plugin.registry

import vdi.config.parse.ConfigurationException
import vdi.core.config.loadAndCacheStackConfig
import vdi.model.meta.DataType
import vdi.model.meta.DatasetType

object PluginRegistry: Iterable<Pair<DatasetType, PluginDetails>> {
  private val mapping: Map<DatasetType, PluginDetails>
  private val typeMeta: Map<DatasetType, PluginDatasetTypeMeta>

  init {
    val conflicts = HashMap<DatasetType, MutableList<String>>(1)
    val tmpCats   = HashMap<DatasetType, PluginDatasetTypeMeta>(12)

    mapping = loadAndCacheStackConfig().vdi.plugins
      .asSequence()
      .flatMap { (name, plug) ->
        val details = PluginDetails(name, plug.projectIDs?.toList() ?: emptyList())

        plug.dataTypes.asSequence()
          .map { DatasetType(DataType.of(it.name), it.version)
            .also { dt -> tmpCats[dt] = PluginDatasetTypeMeta(
              it.category,
              it.maxFileSize,
              it.allowedFileExtensions,
              it.typeChangesEnabled,
              it.usesDataProperties,
              it.dataPropsFileNameSingular,
              it.dataPropsFileNamePlural,
            ) } }
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

    typeMeta = HashMap(tmpCats)
  }

  fun contains(type: DatasetType) = type in mapping

  fun categoryFor(type: DatasetType): String =
    configDataFor(type).category

  fun categoryOrNullFor(type: DatasetType): String? =
    typeMeta[type]?.category

  fun maxFileSizeFor(type: DatasetType): ULong =
    configDataFor(type).maxFileSize

  fun maxFileSizeOrNullFor(type: DatasetType): ULong? =
    typeMeta[type]?.maxFileSize

  fun configDataFor(type: DatasetType): PluginDatasetTypeMeta =
    typeMeta[type]
      ?: throw MissingDataTypeMetaException(type)

  fun configDataOrNullFor(type: DatasetType): PluginDatasetTypeMeta? =
    typeMeta[type]

  operator fun get(type: DatasetType) = mapping[type]

  override fun iterator() = asSequence().iterator()

  fun asSequence() =
    sequence { mapping.forEach { (k, v) -> yield(k to v) } }
}
