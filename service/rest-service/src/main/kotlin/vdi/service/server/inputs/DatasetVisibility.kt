package vdi.service.server.inputs

import vdi.service.generated.model.DatasetVisibility
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility

fun DatasetVisibility.toInternal() = when (this) {
  DatasetVisibility.PRIVATE   -> VDIDatasetVisibility.Private
  DatasetVisibility.PROTECTED -> VDIDatasetVisibility.Protected
  DatasetVisibility.PUBLIC    -> VDIDatasetVisibility.Public
}
