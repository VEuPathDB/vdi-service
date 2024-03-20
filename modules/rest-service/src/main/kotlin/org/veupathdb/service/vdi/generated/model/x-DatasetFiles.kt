package org.veupathdb.service.vdi.generated.model

import vdi.component.db.cache.model.DatasetFile

fun DatasetZipDetails(zipSize: Long, files: List<DatasetFile>): DatasetZipDetails =
  DatasetZipDetailsImpl().also {
    it.zipSize = zipSize
    it.contents = files.map(::DatasetFileDetails)
  }

fun DatasetFileDetails(file: DatasetFile): DatasetFileDetails =
  DatasetFileDetailsImpl().also {
    it.fileName = file.fileName
    it.fileSize = file.fileSize.toLong()
  }