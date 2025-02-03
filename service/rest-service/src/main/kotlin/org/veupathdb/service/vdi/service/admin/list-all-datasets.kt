package org.veupathdb.service.vdi.service.admin

import org.veupathdb.service.vdi.generated.model.*
import org.veupathdb.service.vdi.genx.model.*
import org.veupathdb.service.vdi.util.defaultZone
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import vdi.component.db.app.AppDB
import vdi.component.db.app.model.InstallStatuses
import vdi.component.db.cache.CacheDB
import vdi.component.db.cache.model.AdminAllDatasetsRow
import vdi.component.db.cache.query.AdminAllDatasetsQuery

internal fun listAllDatasets(
  offset: Int?,
  limit: Int?,
  projectID: String?,
  includeDeleted: Boolean?,
): AllDatasetsListResponse {

  val query = AdminAllDatasetsQuery(
    offset    = if (offset == null || offset < 0) 0u else offset.toUInt(),
    limit     = limit?.takeIf { it > 0 }?.toUInt() ?: 100u,
    projectID = projectID,
    includeDeleted ?: false,
  )

  val totalCount = CacheDB().selectAdminAllDatasetCount(query)

  if (totalCount == 0u) {
    return AllDatasetsListResponse(
      AllDatasetsListMeta(0u, query.offset, query.limit, 0u),
      emptyList()
    )
  }

  val cacheDBResults = CacheDB().selectAdminAllDatasets(query)
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

  return AppDB().getDatasetStatuses(projectToDatasetID)
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
    it.shortName = row.shortName
    it.shortAttribution = row.shortAttribution
    it.category = row.category
    it.summary = row.summary
    it.description = row.description
    it.sourceUrl = row.sourceURL
    it.origin = row.origin
    it.projectIds = row.projectIDs
    it.status = DatasetStatusInfo(row.importStatus, statuses)
    it.created = row.created.defaultZone()
    it.isDeleted = row.isDeleted
  }
