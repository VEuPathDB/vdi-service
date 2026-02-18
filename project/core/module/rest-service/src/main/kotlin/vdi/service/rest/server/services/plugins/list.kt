@file:JvmName("PluginService")

package vdi.service.rest.server.services.plugins

import vdi.core.plugin.registry.PluginDatasetTypeMeta
import vdi.core.plugin.registry.PluginRegistry
import vdi.model.meta.DatasetType
import vdi.service.rest.generated.model.PluginDataType
import vdi.service.rest.generated.model.PluginDataTypeImpl
import vdi.service.rest.generated.model.PluginListItem
import vdi.service.rest.generated.model.PluginListItemImpl

internal fun listPlugins(project: String?): List<PluginListItem> {
  var seq = PluginRegistry.asSequence()

  if (project != null) {
    seq = seq.filter { (_, d) -> d.appliesTo(project) }
  }

  val plugins = HashMap<String, PluginListItem>(12)

  seq.forEach { (dt, plug) ->
    plugins.compute(plug.plugin, { _, item ->
      item?.apply { dataTypes.add(PluginDataType(dt)) }
        ?: PluginListItem(dt, plug)
    })
  }

  return plugins.values.toList()
}

private fun PluginListItem(dt: DatasetType, plug: PluginDatasetTypeMeta): PluginListItem =
  PluginListItemImpl().also {
    it.pluginName = plug.plugin
    it.installTargets = plug.installTargets?.asList() ?: emptyList()
    it.dataTypes = mutableListOf(PluginDataType(dt))
  }

private fun PluginDataType(dt: DatasetType): PluginDataType =
  PluginRegistry.require(dt)
    .let { config ->
      PluginDataTypeImpl().apply {
        name = dt.name.toString()
        version = dt.version
        category = config.category
        usesDataProperties = config.usesDataPropertiesFiles
        maxFileSize = config.maxFileSize.toLong()
        allowedFileExtensions = config.allowedFileExtensions.asList()
      }
    }
