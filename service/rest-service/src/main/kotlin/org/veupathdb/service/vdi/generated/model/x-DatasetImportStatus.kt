package org.veupathdb.service.vdi.generated.model

import vdi.component.db.cache.model.DatasetImportStatus as DIS

fun DatasetImportStatus(dis: DIS): DatasetImportStatus = when (dis) {
  DIS.Queued     -> DatasetImportStatus.QUEUED
  DIS.InProgress -> DatasetImportStatus.INPROGRESS
  DIS.Complete   -> DatasetImportStatus.COMPLETE
  DIS.Invalid    -> DatasetImportStatus.INVALID
  DIS.Failed     -> DatasetImportStatus.FAILED
}