package org.veupathdb.service.vdi

import org.veupathdb.lib.container.jaxrs.server.ContainerResources
import org.veupathdb.service.vdi.config.Options
import org.veupathdb.service.vdi.server.controllers.*
import org.veupathdb.service.vdi.server.middleware.AuthFilter

class Resources(opts: Options) : ContainerResources(opts) {
  init {
    registerInstances(AuthFilter(opts))
  }

  override fun resources() = arrayOf<Any>(
    VDIDatasetsAdminController::class.java,
    VDIDatasetByIDEndpointsController::class.java,
    VDIDatasetListEndpointController::class.java,
    VDIDatasetShareGetController::class.java,
    VDIDatasetSharePutController::class.java,
    VDIUsersController::class.java,
    VDIDatasetFilesController::class.java,
    VDICommunityDatasetsController::class.java,
  )
}
