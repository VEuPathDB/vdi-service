package vdi.lib.install

import com.networknt.schema.JsonSchema
import org.veupathdb.vdi.lib.common.field.DataType

data class InstallTarget(
  val enabled: Boolean,
  val name: String,
  val dataTypes: Set<DataType>,
  val propertySchema: JsonSchema,
)
