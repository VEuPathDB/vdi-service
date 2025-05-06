package vdi.service.rest

import org.slf4j.LoggerFactory
import org.veupathdb.lib.container.jaxrs.config.Options
import org.veupathdb.lib.container.jaxrs.providers.DependencyProvider
import org.veupathdb.lib.container.jaxrs.server.Server
import vdi.lib.config.StackConfig
import vdi.lib.config.core.ContainerCoreConfig
import vdi.service.rest.config.ServiceConfig
import vdi.service.rest.health.DependencySource
import vdi.service.rest.s3.DatasetStore

class RestService(private val config: StackConfig) : Server() {
  private val log = LoggerFactory.getLogger(javaClass)

  init {
    DatasetStore.init(config.vdi.objectStore)
  }

  fun main(args: Array<String>) {
    log.info("starting rest-service module")
    DependencyProvider.getInstance().registerDependencySource(DependencySource())
    start(args)
  }

  override fun newResourceConfig(opts: Options) = Resources(opts as ServiceConfig)

  override fun newOptions() = ServiceConfig
}
