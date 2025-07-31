package vdi.service.rest.server.middleware.serde

import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.GenericType
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.MultivaluedMap
import java.io.File
import java.io.InputStream
import java.lang.reflect.Type
import vdi.service.rest.config.ServerContext

class FormParamReader(
  @Context serverContext: ServerContext
): AbstractBodyReader<Any>(serverContext) {
  override fun isReadable(
    type: Class<*>,
    genericType: Type,
    annotations: Array<out Annotation>,
    mediaType: MediaType,
  ) = false // genericType is GenericType<*> && annotations.any { it is FormParam } && mediaType.isUsable(genericType)

  override fun readFrom(
    type: Class<Any>,
    genericType: Type,
    annotations: Array<out Annotation>,
    mediaType: MediaType,
    httpHeaders: MultivaluedMap<String, String>,
    entityStream: InputStream,
  ): Any {
    TODO("Not yet implemented")
  }

  private fun MediaType.isUsable(genericType: GenericType<*>): Boolean {
    return when (type.lowercase()) {
      "text"        -> subtype.equals("plain", true)
      "application" -> appSubTypeIsUsable(genericType)
      else          -> genericType.isFile
    }
  }

  private fun MediaType.appSubTypeIsUsable(genericType: GenericType<*>): Boolean {
    return true
  }

  private val GenericType<*>.isFile get() = rawType == File::class.java
}