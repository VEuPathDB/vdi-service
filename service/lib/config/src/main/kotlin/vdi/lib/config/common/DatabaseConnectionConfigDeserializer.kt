package vdi.lib.config.common

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.contains
import org.veupathdb.vdi.lib.common.field.SecretString

internal class DatabaseConnectionConfigDeserializer: StdDeserializer<DatabaseConnectionConfig>(DirectDatabaseConnectionConfig::class.java) {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): DatabaseConnectionConfig {
    val obj = p.codec.readTree<ObjectNode>(p)

    val user = obj["username"].textValue()
    val pass = SecretString(obj["password"].textValue())

    val platform = obj["platform"]?.textValue()
    val poolSize = obj["poolSize"]?.intValue()?.toUByte()

    return if ("lookupCn" in obj) {
      LDAPDatabaseConnectionConfig(
        username = user,
        password = pass,
        platform = platform,
        poolSize = poolSize,
        lookupCN = obj["lookupCn"].textValue(),
        schema   = obj["schema"]?.textValue(),
      )
    } else {
      DirectDatabaseConnectionConfig(
        username = user,
        password = pass,
        platform = platform!!,
        poolSize = poolSize,
        host     = HostAddress(obj["host"].textValue(), obj["port"].intValue().toUShort()),
        dbName   = obj["dbName"].textValue(),
        schema   = obj["schema"]?.textValue(),
      )
    }
  }
}
