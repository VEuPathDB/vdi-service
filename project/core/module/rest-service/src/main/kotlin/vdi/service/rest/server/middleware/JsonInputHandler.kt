package vdi.service.rest.server.middleware

import com.fasterxml.jackson.databind.DatabindException
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.container.ResourceInfo
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.MultivaluedMap
import jakarta.ws.rs.ext.MessageBodyReader
import java.io.InputStream
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import vdi.json.JSON

class JsonInputHandler(
  private val additionalDeserializableTypes: Set<String>,
  private val generatedModelPackage: String,
): MessageBodyReader<Any> {
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

  @Context
  private lateinit var resource: ResourceInfo

  override fun isReadable(
    type: Class<*>,
    genericType: Type?,
    annotations: Array<out Annotation>?,
    mediaType: MediaType,
  ) = mediaType.subtype == "json"

  override fun readFrom(
    type: Class<Any>,
    genericType: Type?,
    annotations: Array<out Annotation>?,
    mediaType: MediaType,
    httpHeaders: MultivaluedMap<String, String>,
    entityStream: InputStream,
  ): Any? {
    val deserializableType = when {
      Collection::class.java.isAssignableFrom(type) -> {
        if (!(genericType as ParameterizedType).isUsable())
          throw IllegalStateException("cannot deserialize collection with generic parameter(s) $genericType")
        else
          handleCollection(type, genericType)
      }

      Map::class.java.isAssignableFrom(type) -> {
        if (!(genericType as ParameterizedType).isUsable())
          throw IllegalStateException("cannot deserialize map with generic parameter(s) $genericType")
        else
          handleMap(type, genericType)
      }

      type.isUsable() -> {
        JSON.typeFactory.constructType(type)
      }

      else -> throw IllegalStateException("cannot deserialize type $type")
    }

    return try {
      JSON.readValue(entityStream, deserializableType)
    } catch (e: MismatchedInputException) {
      throw BadRequestException("submitted JSON body could not be deserialized into the expected type.")
    }
  }

  @Suppress("UNCHECKED_CAST")
  private fun handleCollection(type: Class<*>, genericType: ParameterizedType) =
    JSON.typeFactory.constructCollectionType(
      type as Class<out Collection<*>>,
      genericType.actualTypeArguments[0] as Class<*>,
    )

  @Suppress("UNCHECKED_CAST")
  private fun handleMap(type: Class<*>, genericType: ParameterizedType) =
    JSON.typeFactory.constructMapType(
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

  private fun Type.isUsable(): Boolean = when (val v = typeName) {
    in deserializableStdLibTypes -> true
    in additionalDeserializableTypes -> true
    else -> v.startsWith(generatedModelPackage)
  }

}