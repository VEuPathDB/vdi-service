package vdi.lib.config.vdi

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.convertValue
import org.veupathdb.vdi.lib.common.field.SecretString
import org.veupathdb.vdi.lib.json.JSON
import vdi.lib.config.common.DatabaseConnectionConfigDeserializer.Companion.deserialize
import vdi.lib.config.common.DirectDatabaseConnectionConfig
import vdi.lib.config.common.HostAddress

internal class InstallTargetConfigDeserializer: StdDeserializer<InstallTargetConfig>(InstallTargetConfig::class.java) {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): InstallTargetConfig {
    val obj = p.codec.readTree<ObjectNode>(p)

    val targetName = obj["targetName"].textValue()
    val dataTypes  = (obj["dataTypes"] as ArrayNode?)
      ?.let { it.mapTo(HashSet(it.size())) { e -> e.textValue() } }
      ?: emptySet()
    val propSchema = JSON.convertValue<DatasetPropertySchema?>(obj["datasetPropertySchema"])


    if (obj["enabled"]?.booleanValue() == false) {
      val dummyDB =
        DirectDatabaseConnectionConfig("disabled", SecretString("disabled"), null, null, "disabled", HostAddress("disabled", null), "disabled")

      return InstallTargetConfig(
        enabled = false,
        targetName = targetName,
        controlDB = dummyDB,
        dataDB = dummyDB,
      )
    }

    return InstallTargetConfig(
      enabled               = true,
      targetName            = targetName,
      dataTypes             = dataTypes,
      controlDB             = (obj["controlDb"] as ObjectNode).deserialize(),
      dataDB                = (obj["dataDb"] as ObjectNode).deserialize(),
      datasetPropertySchema = propSchema ?: NullPropertySchema
    )
  }
}
