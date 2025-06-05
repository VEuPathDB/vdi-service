package vdi.model.data

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize(using = UserIDDeserializer::class)
sealed interface UserID {
  fun toLong(): Long

  @JsonValue
  override fun toString(): String
}

class UserIDDeserializer : JsonDeserializer<UserID>() {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): UserID {
    val node = p.codec.readTree<JsonNode>(p)

    return if (node.isIntegralNumber)
      UserID(node.longValue())
    else if (node.isTextual)
      UserID(node.textValue())
    else
      throw JsonParseException(p, "node was expected to be text or integral but was neither")
  }
}