package org.veupathdb.service.vdi

import org.veupathdb.lib.container.jaxrs.server.ContainerResources
import org.veupathdb.service.vdi.config.Options
import org.veupathdb.service.vdi.server.controllers.*

class Resources(opts: Options) : ContainerResources(opts) {
  init {
    enableAuth()
  }

  override fun resources() = arrayOf<Any>(
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
