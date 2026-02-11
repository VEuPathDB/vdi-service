package vdi.service.rest.server.outputs

import vdi.core.db.app.model.InstallStatus
import vdi.core.db.app.model.InstallStatusDetails
import vdi.core.db.app.model.InstallStatuses
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.model.DatasetUploadStatus
import vdi.model.meta.InstallTargetID
import vdi.model.misc.UploadErrorReport
import vdi.service.rest.generated.model.*
import vdi.service.rest.generated.model.DatasetImportStatusCode as ExternalImportStatus

fun DatasetStatusInfo_(
  uploadStatus:    DatasetUploadStatus,
  uploadMessages:  UploadErrorReport?,
  importStatus:    DatasetImportStatus?,
  importMessages:  List<String>?,
  installStatuses: Map<InstallTargetID, InstallStatuses?>?,
): DatasetStatusInfo =
  DatasetStatusInfoImpl().apply {
    upload  = uploadMessages?.let(::DatasetUploadStatusInfo)
      ?: DatasetUploadStatusInfo(running = importStatus == null)
    import  = importStatus?.let { DatasetImportStatusDetails(it, importMessages) }
    install = installStatuses?.map(::DatasetInstallStatusListEntry)
  }

fun DatasetStatusInfo_(
  uploadStatus:    DatasetUploadStatus,
  importStatus:    DatasetImportStatus?,
  installStatuses: Map<InstallTargetID, InstallStatuses?>?,
): DatasetStatusInfo =
  DatasetStatusInfoImpl().apply {
    upload  = DatasetUploadStatusInfo(uploadStatus)
    import  = importStatus?.let(::DatasetImportStatusInfo)
    install = installStatuses?.map(::DatasetInstallStatusListEntry)
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

private fun DatasetImportStatusInfo(
  status:   DatasetImportStatus?,
  messages: List<String>? = null,
): DatasetImportStatusInfo =
  DatasetImportStatusInfoImpl().also {
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

private fun DatasetUploadStatusInfo(status: DatasetUploadStatus): DatasetUploadStatusInfo =
  DatasetUploadStatusInfoImpl().also {
    it.status = status.toExternal()
  }

private fun DatasetUploadStatus.toExternal() =
  when (this) {
    DatasetUploadStatus.Success -> DatasetUploadStatusCode.COMPLETE
    DatasetUploadStatus.Running -> DatasetUploadStatusCode.RUNNING
    DatasetUploadStatus.Failed  -> DatasetUploadStatusCode.FAILED
  }

private fun DatasetUploadStatusInfo(report: UploadErrorReport): DatasetUploadStatusInfo =
  DatasetUploadStatusInfoImpl().apply {
    status  = DatasetUploadStatusCode.FAILED
    message = report.message
  }
