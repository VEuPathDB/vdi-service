package vdi.lib.config.common

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode

internal class HostAddressListDeserializer: StdDeserializer<List<HostAddress>>(List::class.java) {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext) =
    if (p.currentToken.isScalarValue)
      parseCSV(p.codec.readValue(p, String::class.java))
    else
      parseArray(p.codec.readValue(p, ArrayNode::class.java))

  private fun parseCSV(raw: String) =
    raw.splitToSequence(',')
      .map { HostAddressDeserializer.parseFromString(it) }
      .toList()

  private fun parseArray(raw: ArrayNode) =
    if (raw[0].isObject)
      parseObjectArray(raw)
    else
      parseStringArray(raw)

  private fun parseObjectArray(raw: ArrayNode) =
    raw.asSequence()
      .map { HostAddressDeserializer.parseFromObject(it as ObjectNode) }
      .toList()

  private fun parseStringArray(raw: ArrayNode) =
    raw.asSequence()
      .map { HostAddressDeserializer.parseFromString(it.textValue()) }
      .toList()
}
