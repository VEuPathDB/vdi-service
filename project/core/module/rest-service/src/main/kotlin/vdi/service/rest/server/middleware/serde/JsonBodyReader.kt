package vdi.service.rest.server.middleware.serde

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.MultivaluedMap
import jakarta.ws.rs.ext.MessageBodyReader
import java.io.InputStream
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import vdi.service.rest.config.ServerContext

class JsonBodyReader(@Context private val serverContext: ServerContext): MessageBodyReader<Any> {
  companion object {
    @JvmStatic
    val deserializableStdLibTypes = setOf(
      "boolean",
      "byte",
      "char",
      "double",
      "float",
      "int",
      "long",
      "short",
      "java.lang.Boolean",
      "java.lang.Byte",
      "java.lang.Character",
      "java.lang.Double",
      "java.lang.Float",
      "java.lang.Integer",
      "java.lang.Long",
      "java.lang.Short",
      "java.lang.String",
      "java.math.BigDecimal",
      "java.math.BigInteger",
      "java.util.ArrayList",
      "java.util.HashMap",
      "java.util.HashSet",
      "java.util.LinkedHashMap",
      "java.util.LinkedHashSet",
      "java.util.LinkedList",
      "java.util.List",
      "java.util.Set",
      "java.util.Map",
      "kotlin.UByte",
      "kotlin.UInt",
      "kotlin.ULong",
      "kotlin.UShort",
    )
  }

  private inline val jsonMapper get() = serverContext.jsonMapper

  override fun isReadable(
    type: Class<*>,
    genericType: Type,
    annotations: Array<out Annotation>,
    mediaType: MediaType,
  ) = mediaType.subtype == "json" && (mediaType.type == "application" || mediaType.type == "text")

  override fun readFrom(
    type: Class<Any>,
    genericType: Type,
    annotations: Array<out Annotation>,
    mediaType: MediaType,
    httpHeaders: MultivaluedMap<String, String>,
    entityStream: InputStream,
  ): Any? {
    var generated = false
    val deserializableType = when {
      Collection::class.java.isAssignableFrom(type) -> {
        if (!(genericType as ParameterizedType).isUsable())
          throw IllegalStateException("cannot deserialize collection with generic parameter(s) $genericType(${genericType.actualTypeArguments.joinToString(", ")})")
        else
          handleCollection(type, genericType)
      }

      Map::class.java.isAssignableFrom(type) -> {
        if (!(genericType as ParameterizedType).isUsable())
          throw IllegalStateException("cannot deserialize map with generic parameter(s) $genericType")
        else
          handleMap(type, genericType)
      }

      type.isGenerated() -> {
        generated = true
        jsonMapper.typeFactory.constructType(type)
      }

      type.isUsable() -> jsonMapper.typeFactory.constructType(type)

      else -> throw IllegalStateException("cannot deserialize type $type")
    }

    if (!generated)
      return safeParse(entityStream, deserializableType)

    val rawJson = safeParse<JsonNode>(entityStream, jsonMapper.typeFactory.constructType(JsonNode::class.java))

    serverContext.jsonValidator
      .checkInvalid(type, rawJson)
      ?.also { throw WebApplicationException(it) }

    return jsonMapper.convertValue<Any?>(rawJson, deserializableType)
  }

  private fun <T> safeParse(stream: InputStream, type: JavaType): T {
    return try {
      jsonMapper.readValue(stream, type)
    } catch (e: MismatchedInputException) {
      throw BadRequestException("request json did not match the expected type")
    } catch (e: JsonParseException) {
      throw BadRequestException("malformed json")
    }

  }


  @Suppress("UNCHECKED_CAST")
  private fun handleCollection(type: Class<*>, genericType: ParameterizedType) =
    jsonMapper.typeFactory.constructCollectionType(
      type as Class<out Collection<*>>,
      genericType.actualTypeArguments[0] as Class<*>,
    )

  @Suppress("UNCHECKED_CAST")
  private fun handleMap(type: Class<*>, genericType: ParameterizedType) =
    jsonMapper.typeFactory.constructMapType(
      type as Class<out MutableMap<*, *>>,
      genericType.actualTypeArguments[0] as Class<*>,
      genericType.actualTypeArguments[1] as Class<*>,
    )

  private fun ParameterizedType.isUsable(): Boolean {
    for (type in actualTypeArguments) {
      if (type is ParameterizedType && !type.isUsable())
        return false
      else if (!type.isUsable())
        return false
    }

    return true
  }

  private fun Type.isGenerated() = typeName.startsWith(serverContext.generatedSourcePackage)

  private fun Type.isUsable() = typeName in deserializableStdLibTypes
}