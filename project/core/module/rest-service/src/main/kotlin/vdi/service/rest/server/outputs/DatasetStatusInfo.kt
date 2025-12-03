package vdi.service.rest.server.outputs

import vdi.core.db.app.model.InstallStatus
import vdi.core.db.app.model.InstallStatusDetails
import vdi.core.db.app.model.InstallStatuses
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.model.meta.InstallTargetID
import vdi.service.rest.generated.model.*
import vdi.service.rest.generated.model.DatasetImportStatus as ExternalImportStatus

fun DatasetStatusInfo(
  importStatus:    DatasetImportStatus?,
  importMessages:  List<String>?,
  installStatuses: Map<InstallTargetID, InstallStatuses?>
): DatasetStatusInfo =
  DatasetStatusInfoImpl().also {
    it.import  = DatasetImportStatusDetails(importStatus, importMessages)
    it.install = installStatuses.map(::DatasetInstallStatusListEntry)
  }

@Suppress("NOTHING_TO_INLINE")
private inline fun DatasetInstallStatusListEntry(entry: Map.Entry<InstallTargetID, InstallStatuses?>) =
  DatasetInstallStatusListEntry(entry.key, entry.value)

private fun DatasetInstallStatusListEntry(project: InstallTargetID, status: InstallStatuses?): DatasetInstallStatusListEntry =
  DatasetInstallStatusListEntryImpl().also {
    it.installTarget = project

    it.meta = status?.meta?.let(::DatasetInstallStatusDetails) ?: DatasetInstallStatusDetails()
    it.data = status?.data?.let(::DatasetInstallStatusDetails)
  }

private fun DatasetInstallStatusDetails(): DatasetInstallStatusDetails =
  DatasetInstallStatusDetailsImpl().also { it.status = DatasetInstallStatus.QUEUED }

private fun DatasetInstallStatusDetails(info: InstallStatusDetails): DatasetInstallStatusDetails =
  DatasetInstallStatusDetailsImpl().also {
    it.status   = DatasetInstallStatus(info.status)
    it.messages = info.messages
  }

private fun DatasetImportStatusDetails(
  status:   DatasetImportStatus?,
  messages: List<String>?,
): DatasetImportStatusDetails =
  DatasetImportStatusDetailsImpl().also {
    it.status   = status?.let(::DatasetImportStatus) ?: ExternalImportStatus.QUEUED
    it.messages = messages
  }

@Suppress("NOTHING_TO_INLINE")
private inline fun DatasetInstallStatus(dis: InstallStatus): DatasetInstallStatus = when (dis) {
  InstallStatus.Running            -> DatasetInstallStatus.RUNNING
  InstallStatus.Complete           -> DatasetInstallStatus.COMPLETE
  InstallStatus.FailedValidation   -> DatasetInstallStatus.FAILEDVALIDATION
  InstallStatus.FailedInstallation -> DatasetInstallStatus.FAILEDINSTALLATION
  InstallStatus.MissingDependency  -> DatasetInstallStatus.MISSINGDEPENDENCY
  InstallStatus.ReadyForReinstall  -> DatasetInstallStatus.READYFORREINSTALL
}

@Suppress("NOTHING_TO_INLINE")
private inline fun DatasetImportStatus(dis: DatasetImportStatus): ExternalImportStatus = when (dis) {
  DatasetImportStatus.Queued     -> ExternalImportStatus.QUEUED
  DatasetImportStatus.InProgress -> ExternalImportStatus.INPROGRESS
  DatasetImportStatus.Complete   -> ExternalImportStatus.COMPLETE
  DatasetImportStatus.Invalid    -> ExternalImportStatus.INVALID
  DatasetImportStatus.Failed     -> ExternalImportStatus.FAILED
}
