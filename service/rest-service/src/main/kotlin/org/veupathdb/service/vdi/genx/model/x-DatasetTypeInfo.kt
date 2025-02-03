package org.veupathdb.service.vdi.genx.model

import org.veupathdb.service.vdi.generated.model.DatasetTypeInfo
import org.veupathdb.service.vdi.generated.model.DatasetTypeInfoImpl
import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import vdi.component.db.cache.model.DatasetRecord
import vdi.component.plugin.mapping.PluginHandlers


internal fun DatasetTypeInfo(rec: DatasetRecord, typeName: String): DatasetTypeInfo =
  DatasetTypeInfo(rec.typeName, rec.typeVersion, typeName)

internal fun DatasetTypeInfo(info: VDIDatasetType, displayName: String): DatasetTypeInfo =
  DatasetTypeInfo(info.name, info.version, displayName)

internal fun DatasetTypeInfo(name: DataType, version: String, displayName: String): DatasetTypeInfo =
  DatasetTypeInfoImpl().also {
    it.name = name.toString()
    it.version = version
    it.displayName = displayName
  }

internal fun DatasetTypeInfo(name: DataType, version: String): DatasetTypeInfo =
  DatasetTypeInfo(name, version, PluginHandlers[name, version]?.displayName
      ?: throw IllegalStateException("missing plugin handler for plugin $name $version"))
