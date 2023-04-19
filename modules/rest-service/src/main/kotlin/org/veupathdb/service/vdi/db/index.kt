package org.veupathdb.service.vdi.db

import org.veupathdb.lib.container.jaxrs.health.DatabaseDependency
import org.veupathdb.lib.container.jaxrs.health.Dependency
import org.veupathdb.vdi.lib.db.app.AppDatabaseRegistry
import org.veupathdb.vdi.lib.db.cache.CacheDB


fun initDatabaseDependencies(): Array<Dependency> {
  val out = ArrayList<Dependency>(13)

  for ((name, ds) in AppDatabaseRegistry) {
    val dd = DatabaseDependency(name, ds.host, ds.port.toInt(), ds.source)
    dd.setTestQuery("SELECT 1 FROM dual")
    out.add(dd)
  }

  out.add(DatabaseDependency(CacheDB.details.name, CacheDB.details.host, CacheDB.details.port.toInt(), CacheDB.dataSource))

  return out.toTypedArray()
}
