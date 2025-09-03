@file:JvmName("DatasetVisibilityInputAdaptor")
package vdi.service.rest.server.inputs

import vdi.model.data.DatasetVisibility
import vdi.service.rest.generated.model.DatasetVisibility as APIVisibility

fun APIVisibility.toInternal() = when (this) {
  APIVisibility.PRIVATE   -> DatasetVisibility.Private
  APIVisibility.PROTECTED -> DatasetVisibility.Protected
  APIVisibility.PUBLIC    -> DatasetVisibility.Public
}
