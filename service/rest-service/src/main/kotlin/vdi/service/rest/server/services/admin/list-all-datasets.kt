package vdi.service.rest.server.services.admin

import vdi.service.generated.model.*
import vdi.service.server.outputs.DatasetStatusInfo
import vdi.service.server.outputs.DatasetTypeResponseBody
import vdi.service.server.outputs.DatasetVisibility
import vdi.service.util.defaultZone
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import vdi.component.db.app.AppDB
import vdi.component.db.app.model.InstallStatuses
import vdi.component.db.cache.CacheDB
import vdi.component.db.cache.model.AdminAllDatasetsRow
import vdi.component.db.cache.query.AdminAllDatasetsQuery
import vdi.service.rest.generated.resources.AdminReports.GetAdminReportsDatasetsListAllResponse
import vdi.service.rest.generated.resources.AdminReports.GetAdminReportsDatasetsListAllResponse.respond200WithApplicationJson
import vdi.service.server.inputs.toSafeLimit
import vdi.service.server.inputs.toSafeOffset

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
  meta: vdi.service.rest.generated.model.AllDatasetsListMeta,
  results: List<vdi.service.rest.generated.model.AllDatasetsListEntry>,
): vdi.service.rest.generated.model.AllDatasetsListResponse =
  vdi.service.rest.generated.model.AllDatasetsListResponseImpl().also {
    it.meta = meta
    it.results = results
  }

private fun AllDatasetsListMeta(
  count: UInt,
  offset: UInt,
  limit: UInt,
  total: UInt,
): vdi.service.rest.generated.model.AllDatasetsListMeta =
  vdi.service.rest.generated.model.AllDatasetsListMetaImpl().also {
    it.count = count.toInt()
    it.offset = offset.toInt()
    it.limit = limit.toInt()
    it.total = total.toInt()
  }

private fun AllDatasetsListEntry(
  row: AdminAllDatasetsRow,
  statuses: Map<ProjectID, InstallStatuses>
): vdi.service.rest.generated.model.AllDatasetsListEntry =
  vdi.service.rest.generated.model.AllDatasetsListEntryImpl().also {
    it.datasetId = row.datasetID.toString()
    it.owner = row.ownerID.toLong()
    it.datasetType = DatasetTypeResponseBody(row.typeName, row.typeVersion)
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
