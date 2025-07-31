package vdi.service.rest.server.middleware.serde

import jakarta.ws.rs.container.ResourceInfo
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.MultivaluedMap
import jakarta.ws.rs.ext.MessageBodyWriter
import java.io.OutputStream
import java.lang.reflect.Type
import vdi.service.rest.config.ServerContext

class JsonBodyWriter(
  @Context private val resource: ResourceInfo,
  @Context private val serverContext: ServerContext,
): MessageBodyWriter<Any?> {
  override fun isWriteable(p1: Class<*>?, p2: Type?, p3: Array<out Annotation>?, mediaType: MediaType) =
    (mediaType.subtype == "json" && (mediaType.type == "application" || mediaType.type == "text"))
    && resource.resourceMethod.getAnnotation(JsonIgnore::class.java) == null

  override fun writeTo(
    entity: Any?,
    i1: Class<*>?,
    i2: Type?,
    i3: Array<out Annotation>?,
    i4: MediaType?,
    i5: MultivaluedMap<String, Any>?,
    entityStream: OutputStream,
  ) = serverContext.jsonMapper.writeValue(entityStream, entity)

  @Retention(AnnotationRetention.RUNTIME)
  @Target(AnnotationTarget.FUNCTION)
  annotation class JsonIgnore
}