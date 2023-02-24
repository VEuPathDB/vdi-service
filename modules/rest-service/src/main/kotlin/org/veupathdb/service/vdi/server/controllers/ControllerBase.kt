package org.veupathdb.service.vdi.server.controllers

import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.providers.UserProvider

sealed class ControllerBase(protected val request: ContainerRequest) {

  protected val user by lazy { UserProvider.lookupUser(request).orElseThrow() }

  protected val userID get() = user.userID
}
