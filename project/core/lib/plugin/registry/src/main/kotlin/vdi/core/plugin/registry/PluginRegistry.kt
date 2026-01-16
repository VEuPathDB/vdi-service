package vdi.core.plugin.registry

import vdi.config.parse.ConfigurationException
import vdi.core.config.loadAndCacheStackConfig
import vdi.model.meta.DataType
import vdi.model.meta.DatasetType

object PluginRegistry: Iterable<Pair<DatasetType, PluginDatasetTypeMeta>> {
  private val mapping: Map<DatasetType, String>
  private val typeMeta: Map<DatasetType, PluginDatasetTypeMeta>

  init {
    val conflicts = HashMap<DatasetType, MutableList<String>>(1)
    val tmpCats   = HashMap<DatasetType, PluginDatasetTypeMeta>(12)

    mapping = loadAndCacheStackConfig().vdi.plugins
      .asSequence()
      .flatMap { (pluginName, plug) ->
        plug.dataTypes.asSequence()
          .map { DatasetType(DataType.of(it.name), it.version)
            .also { dt -> tmpCats[dt] = PluginDatasetTypeMeta(
              plugin                        = pluginName,
              category                      = it.category,
              maxFileSize                   = it.maxFileSize,
              allowedFileExtensions         = it.allowedFileExtensions,
              typeChangesEnabled            = it.typeChangesEnabled,
              usesDataPropertiesFiles       = it.usesDataProperties,
              varPropertiesFileNameSingular = it.dataPropsFileNameSingular,
              varPropertiesFileNamePlural   = it.dataPropsFileNamePlural,
              installTargets                = it.installTargets ?: emptyArray(),
            ) } }
          .onEach { conflicts.computeIfAbsent(it, { ArrayList(1) }).add(pluginName) }
          .map { it to pluginName }
      }
      .toMap()

    conflicts.asSequence()
      .filter { (_, v) -> v.size > 1 }
      .joinToString("\n") { (k, v) -> "multiple plugins declare type $k: ${v.joinToString(", ")}" }
      .takeUnless { it.isBlank() }
      ?.also { throw ConfigurationException(it) }

    typeMeta = HashMap(tmpCats)
  }

  fun contains(type: DatasetType) = type in typeMeta

  fun pluginFor(type: DatasetType) = mapping[type]!!

  fun require(type: DatasetType): PluginDatasetTypeMeta =
    typeMeta[type]
      ?: throw MissingDataTypeMetaException(type)

  operator fun get(type: DatasetType) = typeMeta[type]

  override fun iterator() = asSequence().iterator()

  fun asSequence() =
    sequence { typeMeta.forEach { (k, v) -> yield(k to v) } }
}
