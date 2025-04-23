package vdi.service.rest

import org.veupathdb.lib.container.jaxrs.server.ContainerResources
import vdi.service.config.Options
import vdi.service.server.controllers.*

class Resources(opts: Options) : ContainerResources(opts) {
  init {
    enableAuth()
  }

  override fun resources() = arrayOf<Any>(
    AdminReports::class.java,
    AdminRPC::class.java,
    CommunityDatasets::class.java,
    DatasetByID::class.java,
    DatasetFiles::class.java,
    DatasetList::class.java,
    DatasetSharePut::class.java,
    PluginInfo::class.java,
    UserInfo::class.java,
  )
}
