package vdi.service.rest

import org.glassfish.jersey.internal.inject.AbstractBinder
import org.glassfish.jersey.server.ServerProperties
import org.veupathdb.lib.container.jaxrs.server.ContainerResources
import vdi.config.raw.ManifestConfig
import vdi.service.rest.config.ServiceConfig
import vdi.service.rest.config.UploadConfig
import vdi.service.rest.server.controllers.*

class Resources(opts: ServiceConfig) : ContainerResources(opts) {
  init {
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
      }
    })
  }

  override fun resources() = arrayOf<Any>(
    AdminReports::class.java,
    AdminRPC::class.java,
    CommunityDatasets::class.java,
    DatasetByID::class.java,
    DatasetFiles::class.java,
    DatasetList::class.java,
    DatasetSharePut::class.java,
    MetaInfo::class.java,
    PluginInfo::class.java,
    UserInfo::class.java,

    DeprecatedCommunityDatasets::class.java,
    DeprecatedDatasetByID::class.java,
    DeprecatedDatasetFiles::class.java,
    DeprecatedDatasetList::class.java,
    DeprecatedDatasetSharePut::class.java,
    DeprecatedPluginInfo::class.java,
    DeprecatedUserInfo::class.java,
  )
}
