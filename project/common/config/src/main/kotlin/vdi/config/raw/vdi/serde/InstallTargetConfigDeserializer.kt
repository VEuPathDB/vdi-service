package vdi.config.raw.vdi.serde

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.convertValue
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import vdi.config.parse.fields.PartialHostAddress
import vdi.config.raw.db.DirectDatabaseConnectionConfig
import vdi.config.raw.db.serde.DatabaseConnectionConfigDeserializer.Companion.deserialize
import vdi.config.raw.vdi.InstallTargetConfig
import vdi.config.raw.vdi.InstallTargetConfig.JsonKey
import vdi.json.JSON
import vdi.model.field.SecretString
import vdi.model.meta.DatasetType

internal class InstallTargetConfigDeserializer: StdDeserializer<InstallTargetConfig>(InstallTargetConfig::class.java) {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): InstallTargetConfig {
    val obj = p.codec.readTree<ObjectNode>(p)

    val targetName = obj[JsonKey.TargetName].textValue()
    val dataTypes  = (obj[JsonKey.DataTypes] as ArrayNode?)?.processDataTypes() ?: emptySet()
    val fileRoot   = obj[JsonKey.FileRoot].textValue()

    if (obj[JsonKey.Enabled]?.booleanValue() == false) {
      val dummyDB =
        DirectDatabaseConnectionConfig("disabled", SecretString("disabled"), null, null, "disabled", PartialHostAddress("disabled", null), "disabled")

      return InstallTargetConfig(
        enabled         = false,
        targetName      = targetName,
        controlDB       = dummyDB,
        dataDB          = dummyDB,
        datasetFileRoot = fileRoot,
      )
    }

    return InstallTargetConfig(
      enabled         = true,
      targetName      = targetName,
      dataTypes       = dataTypes,
      controlDB       = (obj[JsonKey.ControlDB] as ObjectNode).deserialize(),
      dataDB          = (obj[JsonKey.DataDB] as ObjectNode).deserialize(),
      metaValidation  = obj[JsonKey.MetaValidation]
        ?.let { JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012).getSchema(it) },
      datasetFileRoot = fileRoot,
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
