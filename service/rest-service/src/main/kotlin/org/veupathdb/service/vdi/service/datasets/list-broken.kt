package org.veupathdb.service.vdi.service.datasets

import org.veupathdb.service.vdi.generated.model.*
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import vdi.component.db.app.AppDB
import vdi.component.db.app.AppDatabaseRegistry
import vdi.component.db.app.model.*
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
  // Map of project IDs to the datasets that are registered to that project ID.
  // A dataset ID may appear under multiple project IDs.
  val projectToDatasetIDs = HashMap<String, MutableSet<DatasetID>>(12)

  // Map of datasets to install statuses for any failed installs into any of
  // each dataset's target projects.
  val datasetInstallStatuses = HashMap<DatasetID, MutableMap<ProjectID, MutableList<FailedInstallRecord>>>()

  // For each project registered with the VDI service...
  for ((project, _) in AppDatabaseRegistry.iterator()) {

    // Lookup datasets that are in the broken install status and...
    getBrokenDatasets(project).forEach {
      projectToDatasetIDs.computeIfAbsent(project) { HashSet(1) }
        .add(it.datasetID)

      datasetInstallStatuses.computeIfAbsent(it.datasetID) { HashMap(1) }
        .computeIfAbsent(project) { ArrayList(1) }
        .add(it)
    }
  }

  val results = ArrayList<BrokenDatasetDetails>(datasetInstallStatuses.size)

  for ((datasetID, byProject) in datasetInstallStatuses)
    results.add(BrokenDatasetDetailsImpl().also {
      it.datasetId = datasetID.toString()

      val entries = byProject.toList()

      it.datasetType = with(entries[0].second[0]) { DatasetTypeInfo(typeName, typeVersion) }
      it.statuses = ArrayList(entries.size)

      for ((project, failures) in entries)
        it.statuses.add(failures.toStatusEntry(project))
    })

  return BrokenDatasetListingImpl().apply { details = results }
}

private fun getBrokenDatasets(projectID: ProjectID) =
  AppDB().accessor(projectID)!!.selectFailedInstalls()

private fun List<FailedInstallRecord>.toStatusEntry(projectID: ProjectID) =
  BrokenDatasetStatusEntryImpl().also {
    it.projectId = projectID

    if (size == 1) {
      if (get(0).installType == InstallType.Data) {
        it.dataStatus = DatasetInstallStatus(InstallStatus.FailedInstallation)
        it.dataMessage = get(0).installMessage
      } else {
        it.metaStatus = DatasetInstallStatus(InstallStatus.FailedInstallation)
        it.metaMessage = get(0).installMessage
      }
    } else {
      val data: FailedInstallRecord
      val meta: FailedInstallRecord

      if (get(0).installType == InstallType.Data) {
        data = get(0)
        meta = get(1)
      } else {
        data = get(1)
        meta = get(0)
      }

      it.dataStatus = DatasetInstallStatus(InstallStatus.FailedInstallation)
      it.dataMessage = data.installMessage

      it.metaStatus = DatasetInstallStatus(InstallStatus.FailedInstallation)
      it.metaMessage = meta.installMessage
    }
  }
