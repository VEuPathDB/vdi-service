package vdi.config.parse.serde

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode
import vdi.config.parse.fields.PartialHostAddress

class HostAddressDeserializer: StdDeserializer<PartialHostAddress>(PartialHostAddress::class.java) {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext) =
    if (p.currentToken.isScalarValue)
      parseFromString(p.codec.readValue(p, String::class.java))
    else
      parseFromObject(p.codec.readValue(p, ObjectNode::class.java))

  companion object {
    fun parseFromObject(obj: ObjectNode) =
      PartialHostAddress(obj.get("host").textValue(), obj.get("port")?.intValue()?.toUShort())

    fun parseFromString(raw: String): PartialHostAddress {
      val pos = raw.indexOf(':')

      return if (pos < 0) {
        PartialHostAddress(raw, null)
      } else {
        PartialHostAddress(raw.substring(0, pos), raw.substring(pos + 1).toUShort())
      }
    }
  }
}
