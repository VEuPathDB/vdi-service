package org.veupathdb.service.vdi.service.datasets.list

import org.veupathdb.service.vdi.db.internal.CacheDB
import org.veupathdb.service.vdi.generated.model.DatasetListEntry
import org.veupathdb.service.vdi.model.DatasetListQuery

fun fetchUserDatasetList(query: DatasetListQuery): List<DatasetListEntry> {
  return CacheDB.selectDatasetList(query)
}