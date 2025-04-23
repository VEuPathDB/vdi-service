package vdi.service.rest.server.outputs

import vdi.service.rest.generated.model.DatasetVisibility
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility

fun DatasetVisibility(vis: VDIDatasetVisibility) = when (vis) {
  VDIDatasetVisibility.Private   -> vdi.service.rest.generated.model.DatasetVisibility.PRIVATE
  VDIDatasetVisibility.Protected -> vdi.service.rest.generated.model.DatasetVisibility.PROTECTED
  VDIDatasetVisibility.Public    -> vdi.service.rest.generated.model.DatasetVisibility.PUBLIC
}
