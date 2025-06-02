package vdi.service.rest.server.outputs

import vdi.model.data.DatasetVisibility
import vdi.service.rest.generated.model.DatasetVisibility as APIVisibility

fun DatasetVisibility(vis: DatasetVisibility) = when (vis) {
  DatasetVisibility.Private   -> APIVisibility.PRIVATE
  DatasetVisibility.Protected -> APIVisibility.PROTECTED
  DatasetVisibility.Public    -> APIVisibility.PUBLIC
}
