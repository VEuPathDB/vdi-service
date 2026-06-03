package vdi.service.rest.server

import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.server.ServerProperties
import org.veupathdb.lib.container.jaxrs.server.ContainerResources
import vdi.config.raw.ManifestConfig
import vdi.core.config.StackConfig
import vdi.service.rest.config.ServiceConfig
import vdi.service.rest.config.UploadConfig
import vdi.service.rest.server.controllers.AdminRPC
import vdi.service.rest.server.controllers.AdminReportController
import vdi.service.rest.server.controllers.CommunityDatasets
import vdi.service.rest.server.controllers.DatasetByID
import vdi.service.rest.server.controllers.DatasetFiles
import vdi.service.rest.server.controllers.DatasetListController
import vdi.service.rest.server.controllers.DatasetSharePut
import vdi.service.rest.server.controllers.PluginInfo
import vdi.service.rest.server.controllers.ServiceMetaController
import vdi.service.rest.server.controllers.UserInfo

class Resources(opts: ServiceConfig): ContainerResources(opts) {
  init {
    if (opts.authEnabled)
      enableAuth()

    property(ServerProperties.WADL_FEATURE_DISABLE, true)
    property(ServerProperties.BV_FEATURE_DISABLE, true)
    property(ServerProperties.JSON_BINDING_FEATURE_DISABLE, true)
    property(ServerProperties.JSON_PROCESSING_FEATURE_DISABLE, true)
    property(ServerProperties.MOXY_JSON_FEATURE_DISABLE, true)

    if (opts.enableTrace)
      enableJerseyTrace()

    register(object: AbstractBinder() {
      override fun configure() {
        bind(opts.uploads).to(UploadConfig::class.java)
        bind(opts.manifestConfig).to(ManifestConfig::class.java)
        bind(opts.stackConfig).to(StackConfig::class.java)
      }
    })
  }

  override fun resources() =
    arrayOf<Any>(
      AdminReportController::class.java,
      AdminRPC::class.java,
      CommunityDatasets::class.java,
      DatasetByID::class.java,
      DatasetFiles::class.java,
      DatasetListController::class.java,
      DatasetSharePut::class.java,
      ServiceMetaController::class.java,
      PluginInfo::class.java,
      UserInfo::class.java
    )
}