package org.veupathdb.service.vdi.server.inputs

import org.veupathdb.service.vdi.generated.model.DatasetVisibility
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility

fun DatasetVisibility.toInternal() = when (this) {
  DatasetVisibility.PRIVATE   -> VDIDatasetVisibility.Private
  DatasetVisibility.PROTECTED -> VDIDatasetVisibility.Protected
  DatasetVisibility.PUBLIC    -> VDIDatasetVisibility.Public
}
