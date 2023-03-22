package org.veupathdb.service.vdi.generated.model

import vdi.component.db.cache.model.DatasetImportStatus as DIS

fun DatasetImportStatus(dis: DIS): DatasetImportStatus = when (dis) {
  DIS.AwaitingImport -> DatasetImportStatus.AWAITINGIMPORT
  DIS.Importing      -> DatasetImportStatus.IMPORTING
  DIS.Imported       -> DatasetImportStatus.IMPORTED
  DIS.ImportFailed   -> DatasetImportStatus.IMPORTFAILED
}