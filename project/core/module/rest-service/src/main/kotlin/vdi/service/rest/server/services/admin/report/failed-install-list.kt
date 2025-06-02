package vdi.service.rest.server.services.admin.report

import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID
import vdi.core.db.app.AppDB
import vdi.core.db.app.AppDatabaseRegistry
import vdi.core.db.app.model.DatasetRecord
import vdi.core.db.app.model.InstallStatus
import vdi.core.db.app.model.InstallStatuses
import vdi.core.db.app.model.InstallType
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.service.rest.generated.model.BrokenDatasetInstallDetails
import vdi.service.rest.generated.model.BrokenDatasetInstallDetailsImpl
import vdi.service.rest.generated.model.BrokenDatasetInstallReportBodyImpl
import vdi.service.rest.generated.resources.AdminReports.GetAdminReportsInstallsFailedResponse
import vdi.service.rest.generated.resources.AdminReports.GetAdminReportsInstallsFailedResponse.respond200WithApplicationJson
import vdi.service.rest.server.outputs.DatasetStatusInfo
import vdi.service.rest.server.outputs.DatasetTypeOutput
import vdi.service.rest.server.outputs.toExternal

/**
 * Lists datasets across all registered projects that are in the broken install
 * status [InstallStatus.FailedInstallation].
 *
 * Returns either a list of objects containing details about the datasets, or a
 * simple list of dataset IDs based on whether the [expanded] flag is set to
 * `true`.
 */
internal fun generateFailedInstallReport(expanded: Boolean) =
  if (expanded)
    listExpandedBrokenDatasets()
  else
    listSimpleBrokenDatasets()


/**
 * Lists IDs of datasets across all registered projects that are in the broken
 * install status [InstallStatus.FailedInstallation].
 */
private fun listSimpleBrokenDatasets(): GetAdminReportsInstallsFailedResponse {
  val out = HashSet<DatasetID>()

  for ((project, _) in AppDatabaseRegistry.iterator())
    getBrokenDatasets(project).forEach { out.add(it.datasetID) }

  return respond200WithApplicationJson(BrokenDatasetInstallReportBodyImpl()
    .apply { ids = out.map { it.toString() } })
}


/**
 * Lists details about datasets across all registered projects that are in the
 * broken install status [InstallStatus.FailedInstallation].
 */
private fun listExpandedBrokenDatasets(): GetAdminReportsInstallsFailedResponse {
  // Collect the datasets that are in a broken status into a map to unique the
  // results on dataset ID.
  val cache = HashMap<DatasetID, DatasetRecord>()

  // Map of project IDs to the datasets that are registered to that project ID.
  // A dataset ID may appear under multiple project IDs.
  val projectToDatasetIDs = HashMap<String, MutableSet<DatasetID>>(12)

  // Map of datasets to projects
  val datasetIDToProjects = HashMap<DatasetID, MutableSet<InstallTargetID>>()

  // For each project registered with the VDI service...
  for ((project, _) in AppDatabaseRegistry.iterator()) {

    // Lookup datasets that are in the broken install status and...
    getBrokenDatasets(project).forEach {
      // Record a project to dataset link
      projectToDatasetIDs.computeIfAbsent(project) { HashSet(1) }
        .add(it.datasetID)

      // Record a dataset to project link
      datasetIDToProjects.computeIfAbsent(it.datasetID) { HashSet(1) }
        .add(project)

      // Put the dataset in the cache
      cache[it.datasetID] = it
    }
  }

  // Lookup the statuses for all the target datasets in bulk.
  val datasetInstallStatusMap = AppDB().getDatasetStatuses(projectToDatasetIDs)
  projectToDatasetIDs.clear()

  val results = ArrayList<BrokenDatasetInstallDetails>(cache.size)

  cache.values.forEach {
    results.add(it.toDetails(
      datasetIDToProjects[it.datasetID] ?: emptyList(),
      datasetInstallStatusMap[it.datasetID] ?: emptyMap(),
    ))
  }

  return respond200WithApplicationJson(BrokenDatasetInstallReportBodyImpl()
    .apply { details = results })
}

private fun getBrokenDatasets(installTarget: InstallTargetID) =
  ArrayList<DatasetRecord>(1024).apply {
    for ((_, dataType) in AppDatabaseRegistry) {
      addAll(AppDB().accessor(installTarget, dataType)!!
        .selectDatasetsByInstallStatus(InstallType.Data, InstallStatus.FailedInstallation))
    }
  }

private fun DatasetRecord.toDetails(
  projects: Collection<InstallTargetID>,
  statuses: Map<InstallTargetID, InstallStatuses>,
) =
  BrokenDatasetInstallDetailsImpl().also { out ->
    out.datasetId      = datasetID.toString()
    out.owner          = owner.toLong()
    out.datasetType    = type.toExternal()
    out.installTargets = projects.toList()
    out.status         = DatasetStatusInfo(DatasetImportStatus.Complete, statuses)
  }
