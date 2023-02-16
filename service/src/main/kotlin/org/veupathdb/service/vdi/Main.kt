package org.veupathdb.service.vdi

import org.veupathdb.lib.container.jaxrs.config.Options
import org.veupathdb.lib.container.jaxrs.health.DatabaseDependency
import org.veupathdb.lib.container.jaxrs.health.Dependency
import org.veupathdb.lib.container.jaxrs.server.ContainerResources
import org.veupathdb.lib.container.jaxrs.server.Server
import org.veupathdb.service.vdi.config.Options as Opts

object Main : Server() {
  @JvmStatic
  fun main(args: Array<String>) {
    enableAccountDB()

    // spin up database pools for all the configured databases and register them as dependencies
    start(args)
  }

  override fun newResourceConfig(opts: Options?): ContainerResources {
    TODO("Not yet implemented")
  }

  override fun dependencies(): Array<Dependency> {
    val out = ArrayList<Dependency>(12)

    for (db in Opts.AppDatabases) {
      out.add(DatabaseDependency)
    }
  }

  override fun newOptions() = Opts
}