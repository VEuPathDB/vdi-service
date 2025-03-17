package org.veupathdb.service.vdi

import org.slf4j.LoggerFactory
import org.veupathdb.lib.container.jaxrs.config.Options
import org.veupathdb.lib.container.jaxrs.providers.DependencyProvider
import org.veupathdb.lib.container.jaxrs.server.Server
import org.veupathdb.service.vdi.health.DependencySource
import org.veupathdb.service.vdi.config.Options as Opts

object RestService : Server() {

  private val log = LoggerFactory.getLogger(javaClass)

  @JvmStatic
  fun main(args: Array<String>) {
    log.info("starting rest-service module")
    DependencyProvider.getInstance().registerDependencySource(DependencySource())
    start(args)
  }

  override fun newResourceConfig(opts: Options) = Resources(opts as Opts)

  override fun newOptions() = Opts
}
