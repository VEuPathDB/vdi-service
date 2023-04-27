package org.veupathdb.service.vdi

import org.veupathdb.lib.container.jaxrs.server.ContainerResources
import org.veupathdb.service.vdi.config.Options
import org.veupathdb.service.vdi.server.controllers.VDIDatasetByIDEndpointsController
import org.veupathdb.service.vdi.server.controllers.VDIDatasetListEndpointController

class Resources(opts: Options) : ContainerResources(opts) {
  init {
    enableAuth()
  }

  override fun resources() = arrayOf<Any>(
    VDIDatasetByIDEndpointsController::class.java,
    VDIDatasetListEndpointController::class.java,
  )
}
