package vdi.service.rest.server.inputs

import vdi.service.rest.generated.model.DatasetVisibility
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility

fun vdi.service.rest.generated.model.DatasetVisibility.toInternal() = when (this) {
  vdi.service.rest.generated.model.DatasetVisibility.PRIVATE   -> VDIDatasetVisibility.Private
  vdi.service.rest.generated.model.DatasetVisibility.PROTECTED -> VDIDatasetVisibility.Protected
  vdi.service.rest.generated.model.DatasetVisibility.PUBLIC    -> VDIDatasetVisibility.Public
}
