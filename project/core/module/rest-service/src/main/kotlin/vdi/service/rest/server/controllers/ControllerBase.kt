package vdi.service.rest.server.controllers

import jakarta.ws.rs.core.UriBuilder
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.model.UserInfo
import org.veupathdb.lib.container.jaxrs.providers.UserProvider
import vdi.model.data.DatasetID
import vdi.model.data.UserID
import vdi.model.data.toUserID
import java.net.URI
import vdi.logging.logger

sealed class ControllerBase(val request: ContainerRequest) {
  /**
   * Controller-scoped logger instance.
   *
   * This field is public to allow access by extension methods.
   */
  val logger = logger()

  val urlBase by lazy { request.baseUri.let { URI(it.scheme, it.host, "", "") } }

  /**
   * Base URI used to reach this service.  This value may or may not have a path
   * following the hostname.
   *
   * **Example**
   * ```
   * https://foo.something.com/vdi
   * ```
   */
  val serviceURI: URI
    get() {
      if (Companion.serviceURI == null)
        Companion.serviceURI = request.requestUri
          .let { URI(it.scheme, it.host, it.path.substringBefore(request.uriInfo.path)) }

      return Companion.serviceURI!!
    }


  val maybeUser: UserInfo? by lazy { UserProvider.lookupUser(request).orElse(null) }
  val maybeUserID by lazy { maybeUser?.userId?.toUserID() }

  val user: UserInfo by lazy { UserProvider.lookupUser(request).orElseThrow() }

  private var actualUserID: UserID? = null

  var userID: UserID
    get() {
      if (actualUserID == null)
        actualUserID = UserID(user.userId)
      return actualUserID!!
    }
    protected set(value) {
      actualUserID = value
    }

  fun createURL(path: String): String =
    serviceURI.toString() + path

  fun redirectURL(id: DatasetID) =
    redirectURL(id.toString())

  fun redirectURL(id: String) =
    request.absolutePath.resolve(id).toString()

  companion object {
    private var serviceURI: URI? = null
  }
}
