package org.veupathdb.service.vdi.generated.model

import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import vdi.component.db.cache.model.DatasetRecord
import org.veupathdb.vdi.lib.handler.mapping.PluginHandlers


internal fun DatasetTypeInfo(rec: DatasetRecord, typeName: String): DatasetTypeInfo =
  DatasetTypeInfo(rec.typeName, rec.typeVersion, typeName)

internal fun DatasetTypeInfo(info: VDIDatasetType, displayName: String): DatasetTypeInfo =
  DatasetTypeInfo(info.name, info.version, displayName)

internal fun DatasetTypeInfo(name: String, version: String, displayName: String): DatasetTypeInfo =
  DatasetTypeInfoImpl().also {
    it.name = name.lowercase()
    it.version = version
    it.displayName = displayName
  }

internal fun DatasetTypeInfo(name: String, version: String): DatasetTypeInfo =
  DatasetTypeInfo(name, version, PluginHandlers[name, version]?.displayName
      ?: throw IllegalStateException("missing plugin handler for plugin $name $version"))