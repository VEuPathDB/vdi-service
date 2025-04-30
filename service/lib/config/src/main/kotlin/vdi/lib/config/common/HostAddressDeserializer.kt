package vdi.lib.config.common

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode

internal class HostAddressDeserializer: StdDeserializer<HostAddress>(HostAddress::class.java) {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext) =
    if (p.currentToken.isScalarValue)
      parseFromString(p.codec.readValue(p, String::class.java))
    else
      parseFromObject(p.codec.readValue(p, ObjectNode::class.java))

  companion object {
    fun parseFromObject(obj: ObjectNode) =
      HostAddress(obj.get("host").textValue(), obj.get("port")?.intValue()?.toUShort())

    fun parseFromString(raw: String): HostAddress {
      val pos = raw.indexOf(':')

      return if (pos < 0) {
        HostAddress(raw, null)
      } else {
        HostAddress(raw.substring(0, pos), raw.substring(pos+1).toUShort())
      }
    }
  }
}
