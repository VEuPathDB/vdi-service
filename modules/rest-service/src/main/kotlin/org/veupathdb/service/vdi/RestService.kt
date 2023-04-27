package org.veupathdb.service.vdi

import org.slf4j.LoggerFactory
import org.veupathdb.lib.container.jaxrs.config.Options
import org.veupathdb.lib.container.jaxrs.server.Server
import org.veupathdb.service.vdi.db.initDatabaseDependencies
import org.veupathdb.service.vdi.config.Options as Opts

object RestService : Server() {

  private val log = LoggerFactory.getLogger(javaClass)

  @JvmStatic
  fun main(args: Array<String>) {
    log.info("starting rest-service module")
    enableAccountDB()
    enableUserDB()
    start(args)
  }

  override fun newResourceConfig(opts: Options) = Resources(opts as Opts).apply {
    property("jersey.config.server.tracing.type", "ALL")
    property("jersey.config.server.tracing.threshold", "VERBOSE")
  }

  override fun dependencies() = initDatabaseDependencies()

  override fun newOptions() = Opts
}
