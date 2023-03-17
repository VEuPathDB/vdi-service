package org.veupathdb.service.vdi.service.datasets.list

import vdi.component.db.cache.CacheDB
import org.veupathdb.service.vdi.generated.model.DatasetListEntry
import vdi.component.db.cache.model.DatasetListQuery

fun fetchUserDatasetList(query: DatasetListQuery): List<DatasetListEntry> {
  return CacheDB.selectDatasetList(query)
}