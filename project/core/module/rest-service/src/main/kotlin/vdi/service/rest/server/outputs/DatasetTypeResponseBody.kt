package vdi.service.rest.server.outputs

import vdi.model.data.DataType
import vdi.model.data.DatasetType
import vdi.core.db.cache.model.DatasetRecord
import vdi.core.plugin.registry.PluginRegistry
import vdi.service.rest.generated.model.DatasetTypeOutput as APIType
import vdi.service.rest.generated.model.DatasetTypeOutputImpl


internal fun DatasetTypeOutput(type: DatasetType) =
  DatasetTypeOutputImpl().also {
    it.name        = type.name.toString()
    it.version     = type.version
    it.displayName = PluginRegistry.displayNameOrNullFor(type) ?: type.name.toString()
  }

internal fun DatasetTypeOutput(rec: DatasetRecord, typeName: String) =
  DatasetTypeOutput(rec.type, typeName)

internal fun DatasetTypeOutput(info: DatasetType, displayName: String) =
  DatasetTypeOutput(info.name, info.version, displayName)

internal fun DatasetTypeOutput(name: DataType, version: String, displayName: String): APIType =
  DatasetTypeOutputImpl().also {
    it.name = name.toString()
    it.version = version
    it.displayName = displayName
  }

internal fun DatasetType.toExternal(): APIType =
  DatasetTypeOutputImpl().also {
    it.name = name.toString()
    it.version = version
    it.displayName = PluginRegistry.displayNameOrNullFor(this) ?: ""
  }


