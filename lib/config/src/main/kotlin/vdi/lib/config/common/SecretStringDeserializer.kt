package vdi.lib.config.common

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import org.veupathdb.vdi.lib.common.field.SecretString

internal class SecretStringDeserializer: StdDeserializer<SecretString>(SecretString::class.java) {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext) =
    SecretString(p.codec.readValue(p, String::class.java))
}
