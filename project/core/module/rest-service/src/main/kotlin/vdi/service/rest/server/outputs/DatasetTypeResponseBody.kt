package vdi.service.rest.server.outputs

import vdi.core.plugin.registry.PluginRegistry
import vdi.model.meta.DataType
import vdi.model.meta.DatasetType
import vdi.service.rest.generated.model.DatasetTypeOutputImpl
import vdi.service.rest.generated.model.DatasetTypeOutput as APIType


internal fun DatasetTypeOutput(name: DataType, version: String, category: String): APIType =
  DatasetTypeOutputImpl().also {
    it.name     = name.toString()
    it.version  = version
    it.category = category
  }

internal fun DatasetType.toExternal(): APIType =
  DatasetTypeOutputImpl().also {
    it.name     = name.toString()
    it.version  = version
    it.category = PluginRegistry.require(this).category
  }
