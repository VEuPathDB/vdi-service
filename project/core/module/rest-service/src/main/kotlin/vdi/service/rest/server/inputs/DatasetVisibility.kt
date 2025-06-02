@file:JvmName("DatasetVisibilityInput")
package vdi.service.rest.server.inputs

import vdi.model.data.DatasetVisibility
import vdi.service.rest.generated.model.DatasetVisibility

fun DatasetVisibility.toInternal() = when (this) {
  DatasetVisibility.PRIVATE    -> DatasetVisibility.Private
  DatasetVisibility.PROTECTED  -> DatasetVisibility.Protected
  DatasetVisibility.PUBLIC     -> DatasetVisibility.Public
}
