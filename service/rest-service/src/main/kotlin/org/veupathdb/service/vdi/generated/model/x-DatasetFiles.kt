package org.veupathdb.service.vdi.generated.model

import org.veupathdb.vdi.lib.common.model.VDIDatasetFileInfo

fun DatasetZipDetails(zipSize: Long, files: List<VDIDatasetFileInfo>): DatasetZipDetails =
  DatasetZipDetailsImpl().also {
    it.zipSize = zipSize
    it.contents = files.map(::DatasetFileDetails)
  }

fun DatasetFileDetails(file: VDIDatasetFileInfo): DatasetFileDetails =
  DatasetFileDetailsImpl().also {
    it.fileName = file.filename
    it.fileSize = file.size.toLong()
  }
