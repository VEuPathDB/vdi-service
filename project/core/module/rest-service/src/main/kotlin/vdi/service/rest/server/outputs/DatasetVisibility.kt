package vdi.service.rest.server.outputs

import vdi.model.meta.DatasetVisibility
import vdi.service.rest.generated.model.DatasetVisibility as APIVisibility

fun DatasetVisibility(vis: DatasetVisibility, days: Int) = when (vis) {
  DatasetVisibility.Private -> APIVisibility.PRIVATE
  DatasetVisibility.Public  -> APIVisibility.PUBLIC

  // Controlled vs Protected visibilities:
  //   controlled = visibility == protected && daysForApproval == 0
  //   protected  = visibility == protected && daysForApproval != 0
  DatasetVisibility.Protected ->
    if (days == 0)
      APIVisibility.CONTROLLED
    else
      APIVisibility.PROTECTED
}
