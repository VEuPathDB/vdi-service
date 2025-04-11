package org.veupathdb.service.vdi.server.outputs

import org.veupathdb.service.vdi.generated.model.DatasetTypeResponseBody
import org.veupathdb.service.vdi.generated.model.DatasetTypeResponseBodyImpl
import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import vdi.component.db.cache.model.DatasetRecord
import vdi.component.plugins.PluginRegistry


internal fun DatasetTypeResponseBody(rec: DatasetRecord, typeName: String): DatasetTypeResponseBody =
  DatasetTypeResponseBody(rec.typeName, rec.typeVersion, typeName)

internal fun DatasetTypeResponseBody(info: VDIDatasetType, displayName: String): DatasetTypeResponseBody =
  DatasetTypeResponseBody(info.name, info.version, displayName)

internal fun DatasetTypeResponseBody(name: DataType, version: String, displayName: String): DatasetTypeResponseBody =
  DatasetTypeResponseBodyImpl().also {
    it.name = name.toString()
    it.version = version
    it.displayName = displayName
  }

internal fun DatasetTypeResponseBody(name: DataType, version: String): DatasetTypeResponseBody =
  DatasetTypeResponseBody(name, version, PluginRegistry[name, version]?.displayName
      ?: throw IllegalStateException("missing plugin handler for plugin $name $version"))

internal fun VDIDatasetType.toExternal(): DatasetTypeResponseBody =
  DatasetTypeResponseBodyImpl().also {
    it.name = name.toString()
    it.version = version
    it.displayName = PluginRegistry[name, version]?.displayName ?: ""
  }


