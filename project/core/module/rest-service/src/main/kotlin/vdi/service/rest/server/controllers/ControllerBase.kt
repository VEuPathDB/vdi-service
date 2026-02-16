package vdi.service.rest.server.controllers

import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.model.UserInfo
import org.veupathdb.lib.container.jaxrs.providers.RequestIdProvider
import org.veupathdb.lib.container.jaxrs.providers.UserProvider
import java.net.URI
import vdi.logging.PrefixRequestURI
import vdi.logging.logger
import vdi.logging.mark
import vdi.model.meta.DatasetID
import vdi.model.meta.UserID
import vdi.model.meta.toUserID

sealed class ControllerBase(val request: ContainerRequest) {
  /**
   * Controller-scoped logger instance.
   *
   * This field is public to allow access by extension methods.
   */
  var logger = logger()
    .mark(requestID = RequestIdProvider.getRequestId(request))
    .copy(arrayOf("$PrefixRequestURI=${request.uriInfo.path}"))
    protected set

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
