package vdi.service.rest.server.outputs

import vdi.service.rest.generated.model.DatasetFileDetails
import vdi.service.rest.generated.model.DatasetFileDetailsImpl
import vdi.service.rest.generated.model.DatasetZipDetails
import vdi.service.rest.generated.model.DatasetZipDetailsImpl
import org.veupathdb.vdi.lib.common.model.VDIDatasetFileInfo

fun DatasetZipDetails(zipSize: Long, files: List<VDIDatasetFileInfo>): vdi.service.rest.generated.model.DatasetZipDetails =
  vdi.service.rest.generated.model.DatasetZipDetailsImpl().also {
    it.zipSize = zipSize
    it.contents = files.map(::DatasetFileDetails)
  }

fun DatasetFileDetails(file: VDIDatasetFileInfo): vdi.service.rest.generated.model.DatasetFileDetails =
  vdi.service.rest.generated.model.DatasetFileDetailsImpl().also {
    it.fileName = file.filename
    it.fileSize = file.size.toLong()
  }
