package vdi.service.rest.server.inputs

import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import vdi.service.rest.generated.model.DatasetVisibility

fun DatasetVisibility.toInternal() = when (this) {
  DatasetVisibility.PRIVATE    -> VDIDatasetVisibility.Private
  DatasetVisibility.PROTECTED  -> VDIDatasetVisibility.Protected
  DatasetVisibility.PUBLIC     -> VDIDatasetVisibility.Public
}
