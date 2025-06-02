package vdi.lib.install

import com.networknt.schema.JsonSchema
import vdi.model.data.DataType

data class InstallTargetID(
  val enabled: Boolean,
  val name: String,
  val dataTypes: Set<DataType>,
  val propertySchema: JsonSchema,
)
