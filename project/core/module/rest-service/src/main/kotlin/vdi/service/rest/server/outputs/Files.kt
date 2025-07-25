package vdi.service.rest.server.outputs

import vdi.model.data.DatasetFileInfo
import vdi.service.rest.generated.model.DatasetFileDetails
import vdi.service.rest.generated.model.DatasetFileDetailsImpl
import vdi.service.rest.generated.model.DatasetZipDetails
import vdi.service.rest.generated.model.DatasetZipDetailsImpl

fun DatasetZipDetails(zipSize: Long, files: List<DatasetFileInfo>): DatasetZipDetails =
  DatasetZipDetailsImpl().also {
    it.zipSize = zipSize
    it.contents = files.map(::DatasetFileDetails)
  }

fun DatasetFileDetails(file: DatasetFileInfo): DatasetFileDetails =
  DatasetFileDetailsImpl().also {
    it.fileName = file.name
    it.fileSize = file.size.toLong()
  }

fun DatasetFileDetails(name: String, size: Long): DatasetFileDetails =
  DatasetFileDetailsImpl().also {
    it.fileName = name
    it.fileSize = size
  }
