package vdi.lib.config.vdi

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.contains
import vdi.lib.config.common.HostAddress
import vdi.lib.config.common.SecretString

internal class InstallTargetDeserializer: StdDeserializer<InstallTargetConfigBase>(InstallTargetConfigBase::class.java) {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): InstallTargetConfigBase {
    val obj = p.codec.readTree<ObjectNode>(p)

    val controlSchema = obj["controlSchema"].textValue()
    val dataSchema = obj["dataSchema"].textValue()
    val dataTypes = obj["dataTypes"]?.let { parseDataTypes(p, it as ArrayNode) }
    val enabled = obj["enabled"]?.booleanValue()
    val pass = p.codec.treeToValue(obj["pass"], SecretString::class.java)
    val platform = obj["platform"]?.textValue()
    val poolSize = obj["poolSize"]?.intValue()?.toUByte()
    val targetName = obj["targetName"].textValue()

    return if ("port" in obj)
      DirectInstallTargetConfig(
        controlSchema = controlSchema,
        dataSchema = dataSchema,
        dataTypes = dataTypes,
        enabled = enabled,
        pass = pass,
        platform = platform!!,
        poolSize = poolSize,
        targetName = targetName,
        host = HostAddress(obj["host"].textValue(), obj["port"].intValue().toUShort())
      )
    else if ("host" in obj)
      DirectInstallTargetConfig(
        controlSchema = controlSchema,
        dataSchema = dataSchema,
        dataTypes = dataTypes,
        enabled = enabled,
        pass = pass,
        platform = platform!!,
        poolSize = poolSize,
        targetName = targetName,
        host = p.codec.treeToValue(obj["host"], HostAddress::class.java)
      )
    else
      LDAPInstallTargetConfig(
        controlSchema = controlSchema,
        dataSchema = dataSchema,
        dataTypes = dataTypes,
        enabled = enabled,
        pass = pass,
        platform = platform,
        poolSize = poolSize,
        targetName = targetName,
        ldapCN = obj["ldapCn"].textValue(),
      )
  }

  private fun parseDataTypes(p: JsonParser, raw: ArrayNode): List<String> {
    val out = ArrayList<String>(raw.size())
    raw.forEach { out.add(it.textValue()) }
    return out
  }
}
