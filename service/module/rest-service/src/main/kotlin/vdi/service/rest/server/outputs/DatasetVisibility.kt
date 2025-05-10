package vdi.service.rest.server.outputs

import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import vdi.service.rest.generated.model.DatasetVisibility

fun DatasetVisibility(vis: VDIDatasetVisibility) = when (vis) {
  VDIDatasetVisibility.Private    -> DatasetVisibility.PRIVATE
  VDIDatasetVisibility.Protected  -> DatasetVisibility.PROTECTED
  VDIDatasetVisibility.Controlled -> DatasetVisibility.PROTECTED
  VDIDatasetVisibility.Public     -> DatasetVisibility.PUBLIC
}
