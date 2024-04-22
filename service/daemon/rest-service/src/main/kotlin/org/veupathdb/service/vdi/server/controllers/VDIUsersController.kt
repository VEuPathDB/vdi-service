package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.core.Context
import org.glassfish.jersey.server.ContainerRequest
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.service.vdi.generated.resources.VdiUsers
import org.veupathdb.service.vdi.service.users.getUserMetadata
import org.veupathdb.vdi.lib.common.field.toUserID

@Authenticated(allowGuests = false)
class VDIUsersController(@Context request: ContainerRequest) : VdiUsers, ControllerBase(request) {

  override fun getVdiUsersSelfMeta(): VdiUsers.GetVdiUsersSelfMetaResponse {
    return VdiUsers.GetVdiUsersSelfMetaResponse.respond200WithApplicationJson(getUserMetadata(userID.toUserID()))
  }
}