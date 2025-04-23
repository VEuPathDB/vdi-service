package vdi.service.server.outputs

import vdi.service.generated.model.DatasetVisibility
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility

fun DatasetVisibility(vis: VDIDatasetVisibility) = when (vis) {
  VDIDatasetVisibility.Private   -> DatasetVisibility.PRIVATE
  VDIDatasetVisibility.Protected -> DatasetVisibility.PROTECTED
  VDIDatasetVisibility.Public    -> DatasetVisibility.PUBLIC
}
