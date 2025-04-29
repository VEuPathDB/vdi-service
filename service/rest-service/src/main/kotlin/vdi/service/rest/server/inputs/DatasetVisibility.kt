package vdi.service.rest.server.inputs

import vdi.service.rest.generated.model.DatasetVisibility
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility

fun DatasetVisibility.toInternal() = when (this) {
  DatasetVisibility.PRIVATE    -> VDIDatasetVisibility.Private
  DatasetVisibility.PROTECTED  -> VDIDatasetVisibility.Protected
  DatasetVisibility.CONTROLLED -> VDIDatasetVisibility.Controlled
  DatasetVisibility.PUBLIC     -> VDIDatasetVisibility.Public
}
