package org.veupathdb.service.vdi.generated.model

import org.veupathdb.vdi.lib.db.cache.model.DatasetRecord


fun DatasetTypeInfo(rec: DatasetRecord): DatasetTypeInfo =
  DatasetTypeInfoImpl().also {
    it.name = rec.typeName
    it.version = rec.typeVersion
  }