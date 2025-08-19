package vdi.core.install

import com.networknt.schema.JsonSchema
import vdi.model.data.DatasetType

data class InstallTarget(
  val enabled:       Boolean,
  val name:          String,
  val dataTypes:     Set<DatasetType>,
  val metaValidation: JsonSchema?,
)
