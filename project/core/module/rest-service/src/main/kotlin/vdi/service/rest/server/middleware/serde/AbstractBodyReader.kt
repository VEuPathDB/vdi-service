package vdi.service.rest.server.middleware.serde

import jakarta.ws.rs.ext.MessageBodyReader
import java.lang.reflect.Type
import vdi.service.rest.config.ServerContext

sealed class AbstractBodyReader<T: Any>(
  protected val serverContext: ServerContext
): MessageBodyReader<T> {
  protected val Class<*>.isCollection get() = Collection::class.java.isAssignableFrom(this)

  protected val Class<*>.isMap get() = Map::class.java.isAssignableFrom(this)

  protected val Type.isGenerated get() = typeName.startsWith(serverContext.generatedSourcePackage)
}