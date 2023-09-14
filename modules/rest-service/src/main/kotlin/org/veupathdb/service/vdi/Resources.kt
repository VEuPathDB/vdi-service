package org.veupathdb.service.vdi

import org.veupathdb.lib.container.jaxrs.server.ContainerResources
import org.veupathdb.service.vdi.config.Options
import org.veupathdb.service.vdi.server.controllers.*

class Resources(opts: Options) : ContainerResources(opts) {
  init {
    enableAuth()
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
