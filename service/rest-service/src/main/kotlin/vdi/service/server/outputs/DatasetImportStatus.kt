package vdi.service.server.outputs

import vdi.service.generated.model.DatasetImportStatus
import vdi.component.db.cache.model.DatasetImportStatus as DIS

fun DatasetImportStatus(dis: DIS): DatasetImportStatus = when (dis) {
  DIS.Queued     -> DatasetImportStatus.QUEUED
  DIS.InProgress -> DatasetImportStatus.INPROGRESS
  DIS.Complete   -> DatasetImportStatus.COMPLETE
  DIS.Invalid    -> DatasetImportStatus.INVALID
  DIS.Failed     -> DatasetImportStatus.FAILED
}
