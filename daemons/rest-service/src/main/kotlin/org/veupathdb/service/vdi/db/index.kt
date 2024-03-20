package org.veupathdb.service.vdi.db

import org.veupathdb.lib.container.jaxrs.health.DatabaseDependency
import org.veupathdb.lib.container.jaxrs.health.Dependency
import vdi.component.db.app.AppDatabaseRegistry
import vdi.component.db.cache.CacheDB


fun initDatabaseDependencies(): Array<Dependency> {
  val out = ArrayList<Dependency>(13)

  for ((name, ds) in AppDatabaseRegistry) {
    val dd = DatabaseDependency(name, ds.host, ds.port.toInt(), ds.source)
    dd.setTestQuery("SELECT 1 FROM dual")
    out.add(dd)
  }

  val cacheDB = vdi.component.db.cache.CacheDB()

  out.add(DatabaseDependency(cacheDB.details.name, cacheDB.details.host, cacheDB.details.port.toInt(), cacheDB.dataSource))

  return out.toTypedArray()
}
