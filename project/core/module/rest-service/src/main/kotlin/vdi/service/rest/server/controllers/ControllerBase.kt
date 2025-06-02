package vdi.service.rest.server.controllers

import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.model.UserInfo
import org.veupathdb.lib.container.jaxrs.providers.UserProvider
import vdi.model.data.DatasetID
import vdi.model.data.UserID
import org.veupathdb.vdi.lib.common.field.toUserID
import java.net.URI
import vdi.core.logging.logger

sealed class ControllerBase(val request: ContainerRequest) {
  val logger = logger()

  val urlBase by lazy { request.baseUri.let { URI(it.scheme, it.host, "", "") } }

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
    urlBase.resolve(path).toString()

  fun redirectURL(id: DatasetID) =
    redirectURL(id.toString())

  fun redirectURL(id: String) =
    request.absolutePath.resolve(id).toString()
}
