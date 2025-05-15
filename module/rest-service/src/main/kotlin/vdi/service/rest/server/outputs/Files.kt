package vdi.service.rest.server.outputs

import org.veupathdb.vdi.lib.common.model.VDIDatasetFileInfo
import vdi.service.rest.generated.model.DatasetFileDetails
import vdi.service.rest.generated.model.DatasetZipDetails

fun DatasetZipDetails(zipSize: Long, files: List<VDIDatasetFileInfo>): DatasetZipDetails =
  vdi.service.rest.generated.model.DatasetZipDetailsImpl().also {
    it.zipSize = zipSize
    it.contents = files.map(::DatasetFileDetails)
  }

fun DatasetFileDetails(file: VDIDatasetFileInfo): DatasetFileDetails =
  vdi.service.rest.generated.model.DatasetFileDetailsImpl().also {
    it.fileName = file.filename
    it.fileSize = file.size.toLong()
  }
