package org.veupathdb.service.vdi.generated.model

import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import org.veupathdb.vdi.lib.db.cache.model.DatasetRecord


internal fun DatasetTypeInfo(rec: DatasetRecord): DatasetTypeInfo =
  DatasetTypeInfoImpl().also {
    it.name = rec.typeName
    it.version = rec.typeVersion
  }

internal fun DatasetTypeInfo(info: VDIDatasetType): DatasetTypeInfo =
  DatasetTypeInfoImpl().apply {
    name = info.name
    version = info.version
  }

internal fun DatasetTypeInfo(name: String, version: String): DatasetTypeInfo =
  DatasetTypeInfoImpl().also {
    it.name = name
    it.version = version
  }