package org.veupathdb.service.vdi.generated.model

import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import org.veupathdb.vdi.lib.db.cache.model.DatasetRecord


internal fun DatasetTypeInfo(rec: DatasetRecord, typeName: String): DatasetTypeInfo =
  DatasetTypeInfoImpl().also {
    it.name = rec.typeName
    it.version = rec.typeVersion
    it.displayName = typeName
  }

internal fun DatasetTypeInfo(info: VDIDatasetType, displayName: String): DatasetTypeInfo =
  DatasetTypeInfoImpl().also {
    it.name = info.name
    it.version = info.version
    it.displayName = displayName
  }

internal fun DatasetTypeInfo(name: String, version: String, displayName: String): DatasetTypeInfo =
  DatasetTypeInfoImpl().also {
    it.name = name
    it.version = version
    it.displayName = displayName
  }