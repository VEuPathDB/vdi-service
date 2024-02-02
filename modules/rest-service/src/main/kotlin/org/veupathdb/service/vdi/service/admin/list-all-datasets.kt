package org.veupathdb.service.vdi.service.admin

import org.veupathdb.service.vdi.generated.model.*
import org.veupathdb.service.vdi.util.defaultZone
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.db.app.AppDB
import org.veupathdb.vdi.lib.db.app.model.InstallStatuses
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.model.AdminAllDatasetsRow
import org.veupathdb.vdi.lib.db.cache.query.AdminAllDatasetsQuery

private const val MAX_RESULT_LIMIT = 250u

internal fun listAllDatasets(
  offset: Int?,
  limit: Int?,
  projectID: String?,
): AllDatasetsListResponse {

  val query = AdminAllDatasetsQuery(
    offset    = if (offset == null || offset < 0) 0u else offset.toUInt(),
    limit     = if (limit == null || limit < 0) MAX_RESULT_LIMIT
                else (if (limit > MAX_RESULT_LIMIT.toInt()) MAX_RESULT_LIMIT else limit.toUInt()),
    projectID = projectID
  )

  val totalCount = CacheDB.selectAdminAllDatasetCount(query)

  if (totalCount == 0u) {
    return AllDatasetsListResponse(
      AllDatasetsListMeta(0u, query.offset, query.limit, 0u),
      emptyList()
    )
  }

  val cacheDBResults = CacheDB.selectAdminAllDatasets(query)
  val appDBResults = getAppDBStatuses(cacheDBResults)

  return AllDatasetsListResponse(
    AllDatasetsListMeta(cacheDBResults.size.toUInt(), query.offset, query.limit, totalCount),
    cacheDBResults.asSequence()
      .map { res -> AllDatasetsListEntry(res, appDBResults[res.datasetID] ?: emptyMap()) }
      .toList()
  )
}

private fun getAppDBStatuses(datasets: Collection<AdminAllDatasetsRow>): Map<DatasetID, Map<ProjectID, InstallStatuses>> {
  val projectToDatasetID = HashMap<String, MutableSet<DatasetID>>(datasets.size)

  datasets.forEach { ds ->
    ds.projectIDs.forEach { project ->
      projectToDatasetID.computeIfAbsent(project) { HashSet(1) }
        .add(ds.datasetID)
    }
  }

  return AppDB.getDatasetStatuses(projectToDatasetID)
}

private fun AllDatasetsListResponse(
  meta: AllDatasetsListMeta,
  results: List<AllDatasetsListEntry>,
): AllDatasetsListResponse =
  AllDatasetsListResponseImpl().also {
    it.meta = meta
    it.results = results
  }

private fun AllDatasetsListMeta(
  count: UInt,
  offset: UInt,
  limit: UInt,
  total: UInt,
): AllDatasetsListMeta =
  AllDatasetsListMetaImpl().also {
    it.count = count.toInt()
    it.offset = offset.toInt()
    it.limit = limit.toInt()
    it.total = total.toInt()
  }

private fun AllDatasetsListEntry(
  row: AdminAllDatasetsRow,
  statuses: Map<ProjectID, InstallStatuses>
): AllDatasetsListEntry =
  AllDatasetsListEntryImpl().also {
    it.datasetId = row.datasetID.toString()
    it.owner = row.ownerID.toLong()
    it.datasetType = DatasetTypeInfo(row.typeName, row.typeVersion)
    it.visibility = DatasetVisibility(row.visibility)
    it.name = row.name
    it.summary = row.summary
    it.description = row.description
    it.sourceUrl = row.sourceURL
    it.origin = row.origin
    it.projectIds = row.projectIDs
    it.status = DatasetStatusInfo(row.importStatus, statuses)
    it.created = row.created.defaultZone()
  }
