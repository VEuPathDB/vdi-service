package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.NotFoundException
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.providers.UserProvider
import vdi.components.common.fields.DatasetID
import vdi.components.common.fields.asWDKUserID

sealed class ControllerBase(protected val request: ContainerRequest) {

  protected val user by lazy { UserProvider.lookupUser(request).orElseThrow() }

  protected val userID get() = user.userID

  protected fun String.asVDIID() = try { DatasetID(this) } catch (e: Throwable) { throw NotFoundException() }
}
