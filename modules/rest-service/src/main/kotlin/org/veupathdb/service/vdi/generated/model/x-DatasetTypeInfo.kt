package org.veupathdb.service.vdi.generated.model

import vdi.component.db.cache.model.DatasetRecord

fun DatasetTypeInfo(rec: DatasetRecord): DatasetTypeInfo =
  DatasetTypeInfoImpl().also {
    it.name = rec.typeName
    it.version = rec.typeVersion
  }