package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.NotFoundException
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.model.UserInfo
import org.veupathdb.lib.container.jaxrs.providers.UserProvider
import org.veupathdb.vdi.lib.common.field.DatasetID

sealed class ControllerBase(protected val request: ContainerRequest) {

  protected val maybeUser: UserInfo? by lazy { UserProvider.lookupUser(request).orElse(null) }

  protected val maybeUserID get() = maybeUser?.userId

  protected val user: UserInfo by lazy { UserProvider.lookupUser(request).orElseThrow() }

  protected val userID get() = user.userId

  protected fun String.asVDIID() = try { DatasetID(this) } catch (e: Throwable) { throw NotFoundException() }
}
