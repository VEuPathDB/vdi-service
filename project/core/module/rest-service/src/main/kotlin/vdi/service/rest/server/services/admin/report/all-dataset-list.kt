package vdi.service.rest.server.services.admin.report

import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID
import vdi.core.db.app.AppDB
import vdi.core.db.app.model.InstallStatuses
import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.model.AdminAllDatasetsRow
import vdi.core.db.cache.query.AdminAllDatasetsQuery
import vdi.service.rest.generated.model.*
import vdi.service.rest.generated.resources.AdminReports.GetAdminReportsDatasetsListAllResponse
import vdi.service.rest.generated.resources.AdminReports.GetAdminReportsDatasetsListAllResponse.respond200WithApplicationJson
import vdi.service.rest.server.inputs.toSafeLimit
import vdi.service.rest.server.inputs.toSafeOffset
import vdi.service.rest.server.outputs.DatasetStatusInfo
import vdi.service.rest.server.outputs.DatasetVisibility
import vdi.service.rest.server.outputs.toExternal
import vdi.service.rest.util.defaultZone

internal fun listAllDatasets(
  offset: Int,
  limit: Int,
  projectID: String?,
  includeDeleted: Boolean,
): GetAdminReportsDatasetsListAllResponse {
  val query = AdminAllDatasetsQuery(
    offset         = offset.toSafeOffset(limit),
    limit          = limit.toSafeLimit(100u),
    projectID      = projectID,
    includeDeleted = includeDeleted
  )

  val totalCount = CacheDB().selectAdminAllDatasetCount(query)

  if (totalCount == 0u) {
    return respond200WithApplicationJson(AllDatasetsListResponse(
      AllDatasetsListMeta(0u, query.offset, query.limit, 0u),
      emptyList()
    ))
  }

  val cacheDBResults = CacheDB().selectAdminAllDatasets(query)
  val appDBResults = getAppDBStatuses(cacheDBResults)

  return respond200WithApplicationJson(AllDatasetsListResponse(
    AllDatasetsListMeta(cacheDBResults.size.toUInt(), query.offset, query.limit, totalCount),
    cacheDBResults.asSequence()
      .map { res -> AllDatasetsListEntry(res, appDBResults[res.datasetID] ?: emptyMap()) }
      .toList()
  ))
}

private fun getAppDBStatuses(datasets: Collection<AdminAllDatasetsRow>): Map<DatasetID, Map<InstallTargetID, InstallStatuses>> {
  val projectToDatasetID = HashMap<String, MutableSet<DatasetID>>(datasets.size)

  datasets.forEach { ds ->
    ds.projects.forEach { project ->
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
  statuses: Map<InstallTargetID, InstallStatuses>
): AllDatasetsListEntry =
  AllDatasetsListEntryImpl().also {
    it.datasetId = row.datasetID.toString()
    it.owner = row.ownerID.toLong()
    it.datasetType = row.type.toExternal()
    it.visibility = DatasetVisibility(row.visibility)
    it.name = row.name
    it.shortName = row.shortName
    it.shortAttribution = row.shortAttribution
    it.summary = row.summary
    it.description = row.description
    it.sourceUrl = row.sourceURL
    it.origin = row.origin
    it.installTargets = row.projects
    it.status = DatasetStatusInfo(row.importStatus, statuses)
    it.created = row.created.defaultZone()
    it.isDeleted = row.isDeleted
  }
