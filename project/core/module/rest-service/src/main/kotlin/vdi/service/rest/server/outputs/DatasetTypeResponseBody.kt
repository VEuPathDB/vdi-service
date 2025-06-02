package vdi.service.rest.server.outputs

import vdi.model.data.DataType
import vdi.model.data.DatasetType
import vdi.lib.db.cache.model.DatasetRecord
import vdi.lib.plugin.registry.PluginRegistry
import vdi.service.rest.generated.model.DatasetTypeOutput
import vdi.service.rest.generated.model.DatasetTypeOutputImpl


internal fun DatasetTypeOutput(rec: DatasetRecord, typeName: String): DatasetTypeOutput =
  DatasetTypeOutput(rec.typeName, rec.typeVersion, typeName)

internal fun DatasetTypeOutput(info: DatasetType, displayName: String): DatasetTypeOutput =
  DatasetTypeOutput(info.name, info.version, displayName)

internal fun DatasetTypeOutput(name: DataType, version: String, displayName: String): DatasetTypeOutput =
  DatasetTypeOutputImpl().also {
    it.name = name.toString()
    it.version = version
    it.displayName = displayName
  }

internal fun DatasetTypeOutput(name: DataType, version: String): DatasetTypeOutput =
  DatasetTypeOutput(name, version, PluginRegistry[name, version]?.displayName
      ?: throw IllegalStateException("missing plugin handler for plugin $name $version"))

internal fun DatasetType.toExternal(): DatasetTypeOutput =
  DatasetTypeOutputImpl().also {
    it.name = name.toString()
    it.version = version
    it.displayName = PluginRegistry[name, version]?.displayName ?: ""
  }


