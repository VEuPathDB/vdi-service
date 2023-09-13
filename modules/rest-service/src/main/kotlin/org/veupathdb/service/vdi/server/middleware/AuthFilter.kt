package org.veupathdb.service.vdi.server.middleware

import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ResourceInfo
import jakarta.ws.rs.core.Context
import org.veupathdb.lib.container.jaxrs.config.Options
import org.veupathdb.lib.container.jaxrs.server.middleware.AuthFilter
import org.veupathdb.service.vdi.config.Options as StaticOpts

class AuthFilter(private val opts: Options) : AuthFilter(opts) {

  @Context
  private var resource: ResourceInfo? = null

  override fun filter(req: ContainerRequestContext) {
    if ("Auth-Key" in req.headers) {
      if (StaticOpts.Admin.secretKey == req.headers["Auth-Key"]!![0]) {
        if (haveAdminAuthAnnotation()) {
          return
        }
      }
    }

    super.filter(req)
  }

  fun haveAdminAuthAnnotation(): Boolean {
    val res = resource!!

    res.resourceMethod.declaredAnnotations
      .find { it is AllowAdminAuth }
      ?: return false

    return true
  }

  @Target(AnnotationTarget.FUNCTION)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class AllowAdminAuth
}