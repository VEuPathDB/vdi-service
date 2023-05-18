package org.veupathdb.service.vdi

import org.veupathdb.lib.container.jaxrs.server.ContainerResources
import org.veupathdb.service.vdi.config.Options
import org.veupathdb.service.vdi.server.controllers.VDIDatasetByIDEndpointsController
import org.veupathdb.service.vdi.server.controllers.VDIDatasetListEndpointController
import org.veupathdb.service.vdi.server.controllers.VDIDatasetShareGetController
import org.veupathdb.service.vdi.server.controllers.VDIDatasetSharePutController

class Resources(opts: Options) : ContainerResources(opts) {
  init {
    enableAuth()
  }

  override fun resources() = arrayOf<Any>(
    VDIDatasetByIDEndpointsController::class.java,
    VDIDatasetListEndpointController::class.java,
    VDIDatasetShareGetController::class.java,
    VDIDatasetSharePutController::class.java,
  )
}
