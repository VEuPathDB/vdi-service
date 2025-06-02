package vdi.config.raw.vdi.serde

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.convertValue
import vdi.json.JSON
import vdi.config.parse.fields.PartialHostAddress
import vdi.config.raw.db.DirectDatabaseConnectionConfig
import vdi.config.raw.db.serde.DatabaseConnectionConfigDeserializer.Companion.deserialize
import vdi.config.raw.vdi.InstallTargetConfig
import vdi.config.raw.vdi.characteristics.DatasetPropertySchema
import vdi.config.raw.vdi.characteristics.NullPropertySchema
import vdi.model.data.DatasetType
import vdi.model.field.SecretString

internal class InstallTargetConfigDeserializer: StdDeserializer<InstallTargetConfig>(InstallTargetConfig::class.java) {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): InstallTargetConfig {
    val obj = p.codec.readTree<ObjectNode>(p)

    val targetName = obj["targetName"].textValue()
    val dataTypes  = (obj["dataTypes"] as ArrayNode?)?.processDataTypes() ?: emptySet()
    val propSchema = JSON.convertValue<DatasetPropertySchema?>(obj["datasetPropertySchema"])

    if (obj["enabled"]?.booleanValue() == false) {
      val dummyDB =
        DirectDatabaseConnectionConfig("disabled", SecretString("disabled"), null, null, "disabled", PartialHostAddress("disabled", null), "disabled")

      return InstallTargetConfig(
        enabled = false,
        targetName = targetName,
        controlDB = dummyDB,
        dataDB = dummyDB,
      )
    }

    return InstallTargetConfig(
      enabled = true,
      targetName = targetName,
      dataTypes = dataTypes,
      controlDB = (obj["controlDb"] as ObjectNode).deserialize(),
      dataDB = (obj["dataDb"] as ObjectNode).deserialize(),
      datasetPropertySchema = propSchema ?: NullPropertySchema
    )
  }

  private fun ArrayNode.processDataTypes(): Set<DatasetType> {
    return when {
      isEmpty -> emptySet()
      size() == 1 -> get(0).let { when {
        it.isTextual -> emptySet()
        else         -> null
      } }
      else -> null
    } ?: mapTo(HashSet(size())) { JSON.convertValue(it) }
  }
}
