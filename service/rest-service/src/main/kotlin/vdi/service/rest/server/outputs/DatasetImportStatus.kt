package vdi.service.rest.server.outputs

import vdi.service.rest.generated.model.DatasetImportStatus
import vdi.component.db.cache.model.DatasetImportStatus as DIS

fun DatasetImportStatus(dis: DIS): vdi.service.rest.generated.model.DatasetImportStatus = when (dis) {
  DIS.Queued     -> vdi.service.rest.generated.model.DatasetImportStatus.QUEUED
  DIS.InProgress -> vdi.service.rest.generated.model.DatasetImportStatus.INPROGRESS
  DIS.Complete   -> vdi.service.rest.generated.model.DatasetImportStatus.COMPLETE
  DIS.Invalid    -> vdi.service.rest.generated.model.DatasetImportStatus.INVALID
  DIS.Failed     -> vdi.service.rest.generated.model.DatasetImportStatus.FAILED
}
