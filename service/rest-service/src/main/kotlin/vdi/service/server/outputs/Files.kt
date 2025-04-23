package vdi.service.server.outputs

import vdi.service.generated.model.DatasetFileDetails
import vdi.service.generated.model.DatasetFileDetailsImpl
import vdi.service.generated.model.DatasetZipDetails
import vdi.service.generated.model.DatasetZipDetailsImpl
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
