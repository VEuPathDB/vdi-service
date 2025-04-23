package vdi.service.rest.server.controllers

import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.model.UserInfo
import org.veupathdb.lib.container.jaxrs.providers.UserProvider
import org.veupathdb.vdi.lib.common.field.DatasetID
import java.net.URI

sealed class ControllerBase(protected val request: ContainerRequest) {
  protected val urlBase by lazy { request.baseUri.let { URI(it.scheme, it.host, "", "") } }

  protected val maybeUser: UserInfo? by lazy { UserProvider.lookupUser(request).orElse(null) }

  protected val maybeUserID get() = maybeUser?.userId

  protected val user: UserInfo by lazy { UserProvider.lookupUser(request).orElseThrow() }

  protected val userID get() = user.userId

  protected fun createURL(path: String): String =
    urlBase.resolve(path).toString()

  protected fun redirectURL(id: DatasetID) =
    redirectURL(id.toString())

  protected fun redirectURL(id: String) =
    request.baseUri.toString().replaceAfterLast('/', id)
}
