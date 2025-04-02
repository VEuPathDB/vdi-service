package org.veupathdb.service.vdi.server.controllers

import jakarta.ws.rs.core.Response
import org.veupathdb.service.vdi.generated.support.ResponseDelegate

internal inline fun <reified T: ResponseDelegate> patchResponse(original: ResponseDelegate): T {
  val constructor = T::class.java.getConstructor(Response::class.java, Any::class.java)
  constructor.isAccessible = true
  constructor.newInstance(original.)
}
