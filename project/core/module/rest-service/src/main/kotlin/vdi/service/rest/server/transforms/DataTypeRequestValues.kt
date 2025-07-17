package vdi.service.rest.server.transforms

import vdi.model.data.DataType
import vdi.model.data.DatasetType
import vdi.service.rest.gen.model.DataTypeRequestValues

fun DataTypeRequestValues.toDatasetType(): DatasetType {
  return DatasetType(
    name = DataType.of(name),
    version = version,
  )
}