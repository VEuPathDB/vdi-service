package vdi.service.rest

import org.slf4j.LoggerFactory
import org.veupathdb.lib.container.jaxrs.config.Options
import org.veupathdb.lib.container.jaxrs.providers.DependencyProvider
import org.veupathdb.lib.container.jaxrs.server.Server
import vdi.service.rest.health.DependencySource

object RestService : Server() {
  private val log = LoggerFactory.getLogger(javaClass)

  @JvmStatic
  fun main(args: Array<String>) {
    log.info("starting rest-service module")
    DependencyProvider.getInstance().registerDependencySource(DependencySource())
    start(args)
  }

  override fun newResourceConfig(opts: Options) = Resources(opts as ServiceConfig)

  override fun newOptions() = ServiceConfig
}
