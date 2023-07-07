package org.veupathdb.service.vdi.generated.model

import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility

fun DatasetVisibility.toInternalVisibility() = when (this) {
  DatasetVisibility.PRIVATE   -> VDIDatasetVisibility.Private
  DatasetVisibility.PROTECTED -> VDIDatasetVisibility.Protected
  DatasetVisibility.PUBLIC    -> VDIDatasetVisibility.Public
}

fun DatasetVisibility(vis: VDIDatasetVisibility) = when (vis) {
  VDIDatasetVisibility.Private   -> DatasetVisibility.PRIVATE
  VDIDatasetVisibility.Protected -> DatasetVisibility.PROTECTED
  VDIDatasetVisibility.Public    -> DatasetVisibility.PUBLIC
}