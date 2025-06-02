package vdi.model.data

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonDeserialize(using = UserIDDeserializer::class)
@JsonSerialize(using = UserIDSerializer::class)
sealed interface UserID {
  fun toLong(): Long

  @JsonValue
  override fun toString(): String
}

fun UserID(value: Long): UserID {
  if (value < 1)
    throw IllegalArgumentException("User ID values cannot be less than 1")

  return LongUserID(value)
}

fun UserID(value: String): UserID {
  val long = try {
    value.toLong()
  } catch (e: Throwable) {
    throw IllegalArgumentException("User ID values must be integral numbers.")
  }

  return UserID(long)
}

fun Long.toUserID() = UserID(this)
fun Long.toUserIDOrNull() = try { UserID(this) } catch (e: Throwable) { null }

fun String.toUserID() = UserID(this)
fun String.toUserIDOrNull() = try { UserID(this) } catch (e: Throwable) { null }

@JvmInline
private value class LongUserID(val value: Long): UserID {
  override fun toLong() = value
  override fun toString() = value.toString()
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

class UserIDSerializer : JsonSerializer<UserID>() {
  override fun serialize(value: UserID, gen: JsonGenerator, serializers: SerializerProvider) {
    gen.writeString(value.toString())
  }
}