package vdi.service.rest.server.outputs

import vdi.service.rest.generated.model.DatasetImportStatus
import vdi.core.db.cache.model.DatasetImportStatus as DIS

fun DatasetImportStatus(dis: DIS): DatasetImportStatus = when (dis) {
  DIS.Queued     -> DatasetImportStatus.QUEUED
  DIS.InProgress -> DatasetImportStatus.INPROGRESS
  DIS.Complete   -> DatasetImportStatus.COMPLETE
  DIS.Invalid    -> DatasetImportStatus.INVALID
  DIS.Failed     -> DatasetImportStatus.FAILED
}
