package vdi.service.rest.server.outputs

import vdi.model.data.DatasetVisibility
import vdi.service.rest.generated.model.DatasetVisibility

fun DatasetVisibility(vis: vdi.model.data.DatasetVisibility) = when (vis) {
  DatasetVisibility.Private   -> DatasetVisibility.PRIVATE
  DatasetVisibility.Protected -> DatasetVisibility.PROTECTED
  DatasetVisibility.Public    -> DatasetVisibility.PUBLIC
}
