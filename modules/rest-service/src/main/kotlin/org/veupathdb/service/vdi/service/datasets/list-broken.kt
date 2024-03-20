package org.veupathdb.service.vdi.service.datasets

import org.veupathdb.service.vdi.generated.model.*
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.db.app.AppDB
import org.veupathdb.vdi.lib.db.app.AppDatabaseRegistry
import org.veupathdb.vdi.lib.db.app.model.DatasetRecord
import org.veupathdb.vdi.lib.db.app.model.InstallStatus
import org.veupathdb.vdi.lib.db.app.model.InstallStatuses
import org.veupathdb.vdi.lib.db.app.model.InstallType
import vdi.component.db.cache.model.DatasetImportStatus

/**
 * Lists datasets across all registered projects that are in the broken install
 * status [InstallStatus.FailedInstallation].
 *
 * Returns either a list of objects containing details about the datasets, or a
 * simple list of dataset IDs based on whether the [expanded] flag is set to
 * `true`.
 */
internal fun listBrokenDatasets(expanded: Boolean): BrokenDatasetListing {
  return if (expanded)
    listExpandedBrokenDatasets()
  else
    listSimpleBrokenDatasets()
}


/**
 * Lists IDs of datasets across all registered projects that are in the broken
 * install status [InstallStatus.FailedInstallation].
 */
private fun listSimpleBrokenDatasets(): BrokenDatasetListing {
  val out = HashSet<DatasetID>()

  for ((project, _) in AppDatabaseRegistry.iterator())
    getBrokenDatasets(project).forEach { out.add(it.datasetID) }

  return BrokenDatasetListingImpl().apply { ids = out.map { it.toString() } }
}


/**
 * Lists details about datasets across all registered projects that are in the
 * broken install status [InstallStatus.FailedInstallation].
 */
private fun listExpandedBrokenDatasets(): BrokenDatasetListing {
  // Collect the datasets that are in a broken status into a map to unique the
  // results on dataset ID.
  val cache = HashMap<DatasetID, DatasetRecord>()

  // Map of project IDs to the datasets that are registered to that project ID.
  // A dataset ID may appear under multiple project IDs.
  val projectToDatasetIDs = HashMap<String, MutableSet<DatasetID>>(12)

  // Map of datasets to projects
  val datasetIDToProjects = HashMap<DatasetID, MutableSet<ProjectID>>()

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

  val results = ArrayList<BrokenDatasetDetails>(cache.size)

  cache.values.forEach {
    results.add(it.toDetails(
      datasetIDToProjects[it.datasetID] ?: emptyList(),
      datasetInstallStatusMap[it.datasetID] ?: emptyMap(),
    ))
  }

  return BrokenDatasetListingImpl().apply { details = results }
}

private fun getBrokenDatasets(projectID: ProjectID) =
  AppDB().accessor(projectID)!!
    .selectDatasetsByInstallStatus(InstallType.Data, InstallStatus.FailedInstallation)

private fun DatasetRecord.toDetails(
  projects: Collection<ProjectID>,
  statuses: Map<ProjectID, InstallStatuses>,
) =
  BrokenDatasetDetailsImpl().also { out ->
    out.datasetId   = datasetID.toString()
    out.owner       = owner.toLong()
    out.datasetType = DatasetTypeInfo(typeName, typeVersion)
    out.projectIds  = projects.toList()
    out.status      = DatasetStatusInfo(DatasetImportStatus.Complete, statuses)
  }